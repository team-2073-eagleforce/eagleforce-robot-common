package com.team2073.common.datarecorder;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.concurrency.FutureWait;
import com.team2073.common.config.CommonProperties;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.DataRecorderStateMachine;
import com.team2073.common.datarecorder.model.DelegatingRecordableWrapper;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.datarecorder.model.ObjectWrapper;
import com.team2073.common.datarecorder.model.RecordableWrapper;
import com.team2073.common.datarecorder.output.DataRecordOutputHandler;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerConsoleImpl;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerCsvImpl;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerSmartDashboardImpl;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ThreadUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.team2073.common.datarecorder.model.DataRecorderStateMachine.DisabledCause.*;
import static com.team2073.common.datarecorder.model.DataRecorderStateMachine.InitializationPhase.*;
import static com.team2073.common.util.ClassUtil.*;
import static com.team2073.common.util.ThreadUtil.*;

/**
 * @author pbriggs
 */
public class DataRecorder {

    // Misc/helpers
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DataRecorderHelper mapper = new DataRecorderHelper();
    private DataRecorderStateMachine state = new DataRecorderStateMachine();

    // State
    private LocalDateTime initTimestamp = LocalDateTime.now();
    private boolean initializedOutputHandlers;

    // Instances
    private final Map<RecordableWrapper, DataRecordTable> dataTableMap = new HashMap<>();
    private final Set<DataRecordOutputHandler> outputHandlerList = new HashSet<>();


    // Public configuration methods
    // ============================================================

    public void enable() {
        if (!state.disable.isDisabledManually())
            log.warn("Attempted to enable when already enabled.");

        state.disable.updateDisabledModeToInactive(DISABLED_MANUALLY);
    }

    public void disable() {
        state.disable.updateDisabledModeToActive(DISABLED_MANUALLY);
    }

    public void requestShutdown() {
        state.initialization.requestShutdown();
    }

    public void requestShutdownAndWait() {
        requestShutdown();
        while (!state.initialization.isShutdown()) {
            log.debug("Waiting for shutdown...");
            ThreadUtil.sleep(5);
        }
    }

    // Register Recordable methods
    // ============================================================

    public List<Boolean> registerRecordable(Object... recordableList) {
        return registerRecordable(Arrays.asList(recordableList));
    }

    public List<Boolean> registerRecordable(List<? extends Object> recordableList) {
        List<Boolean> resultList = new ArrayList<>();
        for (Object recordable : recordableList) {
            resultList.add(registerRecordable(recordable));
        }
        return resultList;
    }

    public boolean registerRecordable(Object recordable) {
        return registerRecordable(recordable, getCommonProps().getDataRecorderDefaultRecordInterval());
    }

    // private until interval is actually configured
    private boolean registerRecordable(Object recordable, long period) {
        RobotContext.getInstance().getCommonProps().setDataRecorderDefaultRecordInterval(10L);
        if (recordable instanceof DataRecordOutputHandler) {
            // TODO: Check if it's annotated with @Recordable
            String recordableName = simpleName(recordable);
            String outputHandlerClass = simpleName(DataRecordOutputHandler.class);
            Throw.illegalArg("Attempted to register [{}] (a [{}]) for recording. You most likely meant to call [{}]. " +
                    "If you truly wanted to register a [{}] for recording, use the method [{}].",
                    recordableName, outputHandlerClass, REGISTER_OUTPUT_HANDLER_METHOD_NAME,
                    outputHandlerClass, REGISTER_OUTPUT_HANDLER_AS_RECORDABLE_METHOD_NAME);
        }
        checkRecordable(recordable);
        return registerRecordableInternal(new ObjectWrapper(recordable), period);
    }

    public boolean registerRecordable(LifecycleAwareRecordable recordable) {
        return registerRecordable(recordable, getCommonProps().getDataRecorderDefaultRecordInterval());
    }

    public boolean registerRecordable(LifecycleAwareRecordable recordable, long period) {
        checkRecordable(recordable);
        return registerRecordableInternal(new DelegatingRecordableWrapper(recordable), period);
    }

    private static final String REGISTER_OUTPUT_HANDLER_AS_RECORDABLE_METHOD_NAME = "registerOutputHandlerAsRecordable(...)";

    public boolean registerOutputHandlerAsRecordable(DataRecordOutputHandler recordable) {
        return registerOutputHandlerAsRecordable(recordable, getCommonProps().getDataRecorderDefaultRecordInterval());
    }

    public boolean registerOutputHandlerAsRecordable(DataRecordOutputHandler recordable, long period) {
        checkRecordable(recordable);
        return registerRecordableInternal(new ObjectWrapper(recordable), period);
    }

    private void checkRecordable(Object instance) {
        Assert.assertNotNull(instance, "instance");
        Class<?> clazz = instance.getClass();

        if (Collection.class.isAssignableFrom(instance.getClass()))
            Throw.illegalArg("[{}] does not support recording [{}] types.",
                    simpleName(DataRecorder.class), simpleName(clazz));

        if (clazz.isArray())
            Throw.illegalArg("[{}] does not support recording [{}] types.",
                    simpleName(DataRecorder.class), simpleName(clazz));

        if (ReflectionUtil.isPrimitiveOrWrapper(clazz))
            Throw.illegalArg("[{}] does not support recording primitive or primitive wrapper types. Type: [{}].",
                    simpleName(DataRecorder.class), simpleName(clazz));
    }

    private boolean registerRecordableInternal(RecordableWrapper recordableWrapper, long period) {
        Assert.assertNotNull(recordableWrapper, "recordableWrapper");
        Object recordable = recordableWrapper.getInstance();
        Assert.assertNotNull(recordable, "recordable");
        String recordableName = mapper.generateName(recordable).buildName();

        log.info("Registering Recordable [{}].", recordableName);

        if (state.initialization.startedPreviously())
            Throw.illegalState("Cannot register any more instances when runner has already started. " +
                    "DataRecorderState: [%s].", getStateToString());

        state.initialization.setIntializationPhase(INITIALIZING);

        DataRecordTable dataRecordTable = dataTableMap.get(recordableWrapper);

        if (dataRecordTable != null) {

            // This recordable has already been registered...
            log.warn("Requested to register the same Recordable instance [{}] multiple times. " +
                    "Ignoring...", recordableName);
            return false;
        } else {

            // This recordable doesn't already exist, add it to the map

            List<FieldMapping> fieldMappings = mapper.createFieldMappings(recordable);
            dataRecordTable = new DataRecordTable(recordableWrapper, fieldMappings, recordableName, period, initTimestamp,
                    getCommonProps().getDataRecorderTimestampFormat(), Optional.of(getCommonProps().getDataRecorderCustomTimestampFormat()));

            dataTableMap.put(recordableWrapper, dataRecordTable);
            log.debug("Registering Recordable [{}] complete.", recordableName);
            return true;
        }
    }

    // Register OutputHandler methods
    // ============================================================

    public DataRecorder registerConsoleOutputHandler() {
        return registerOutputHandler(new DataRecordOutputHandlerConsoleImpl());
    }

    public DataRecorder registerCsvOutputHandler() {
        return registerOutputHandler(new DataRecordOutputHandlerCsvImpl());
    }

    public DataRecorder registerSmartDashboardOutputHandler() {
        return registerOutputHandler(new DataRecordOutputHandlerSmartDashboardImpl());
    }

    private static final String REGISTER_OUTPUT_HANDLER_METHOD_NAME = "registerOutputHandler(...)";

    public DataRecorder registerOutputHandler(DataRecordOutputHandler handler) {
        String name = simpleName(handler);
        log.info("Registered output handler [{}].", name);
        if (state.initialization.startedPreviously())
            Throw.illegalState("Cannot register output handler [{}]. [{}] has already started. State: [{}].", name, simpleName(this), getStateToString());

        state.initialization.setIntializationPhase(INITIALIZING);
        outputHandlerList.add(handler);
        return this;
    }

    // Manual methods
    // ============================================================

    private final ExecutorService manualRecordScheduler = Executors.newSingleThreadExecutor(withThreadNamePattern("manual-record"));
    private final ExecutorService manualFlushScheduler = Executors.newSingleThreadExecutor(withThreadNamePattern("manual-flush"));

    public FutureWait<?> manualRecord() {
        // Manual is the only one where we fail in the first method
        state.recording.setManualRecordingActiveOrFail();
        log.trace("Submitting manual record request to async thread.");
        return new FutureWait<>(manualRecordScheduler.submit(manualRecord));
    }

    public FutureWait manualFlush() {
        state.recording.setManualRecordingActiveOrFail();
        log.trace("Submitting manual flush request to async thread.");
        return new FutureWait<>(manualFlushScheduler.submit(manualFlush));
    }

    private final Runnable manualRecord = () -> manualRecordInternal();
    private final Runnable manualFlush = () -> manualFlushInternal();

    private void manualRecordInternal() {
        log.trace("Processing manual record request...");
        ExceptionUtil.suppressVoid(() -> record(), "record()");
        log.trace("Processing manual record request complete.");
    }

    private void manualFlushInternal() {
        log.trace("Processing manual flush request...");
        ExceptionUtil.suppressVoid(() -> flush(), "flush()");
        log.trace("Processing manual flush request complete.");
    }
    // Periodic Runner methods
    // ============================================================

    private final AsyncPeriodicRunnable periodicRecord = () -> periodicRunnerRecord();
    private final AsyncPeriodicRunnable periodicFlush = () -> periodicRunnerFlush();

    /** See {@link #registerWithPeriodicRunner(PeriodicRunner, long)} */
    public void registerWithPeriodicRunner() {
        registerWithPeriodicRunner(getCommonProps().getDataRecorderAutoFlushInterval());
    }

    /** See {@link #registerWithPeriodicRunner(PeriodicRunner, long)} */
    public void registerWithPeriodicRunner(long flushInterval) {
        registerWithPeriodicRunner(RobotContext.getInstance().getPeriodicRunner(), flushInterval);
    }

    /** See {@link #registerWithPeriodicRunner(PeriodicRunner, long)} */
    public void registerWithPeriodicRunner(PeriodicRunner periodicRunner) {
        registerWithPeriodicRunner(periodicRunner, getCommonProps().getDataRecorderAutoFlushInterval());
    }

    /** TODO */
    public void registerWithPeriodicRunner(PeriodicRunner periodicRunner, long flushInterval) {
        if (state.recording.isPeriodicRecordingActive()) {
            log.info("Ignoring call to activate periodic recording with interval of [{}], it is already active.", flushInterval);
            return;
        }

        state.recording.setPeriodicRecordingActiveOrFail();

        if (periodicRunner.isRegistered(periodicRecord) || periodicRunner.isRegistered(periodicFlush)) {
            Throw.illegalState("INTERNAL ERROR: How did this happen? We just verified periodic recording was " +
                    "not active yet we have already registered our **private** periodic instance with periodic recorder.");
            return;
        }

        long shortestInterval = getShortestInterval();

        log.info("Registering record with [{}] with async interval of [{}] ms.", simpleName(periodicRunner), shortestInterval);
        periodicRunner.registerAsync(periodicRecord, "DataRecorder-record", shortestInterval);
        log.info("Registering flush with [{}] with async interval of [{}] ms.", simpleName(periodicRunner), flushInterval);
        periodicRunner.registerAsync(periodicFlush, "DataRecorder-flush", flushInterval);
    }

    private void periodicRunnerRecord() {
        log.trace("Processing PeriodicRunner record request...");
        // No exception handling. Let PeriodicRunner handle so it can monitor properly
        periodicRunnerRecordInternal();
        log.trace("Processing PeriodicRunner record request complete.");
    }

    private void periodicRunnerRecordInternal() {
        state.recording.setPeriodicRecordingActiveOrFail();
        record();
    }

    private void periodicRunnerFlush() {
        log.trace("Processing PeriodicRunner flush request...");
        // No exception handling. Let PeriodicRunner handle so it can monitor properly
        periodicRunnerFlushInternal();
        log.trace("Processing PeriodicRunner flush request complete.");
    }

    private void periodicRunnerFlushInternal() {
        state.recording.setPeriodicRecordingActiveOrFail();
        flush();
    }

    // Auto methods
    // ============================================================

    private final ScheduledExecutorService autoRecordScheduler = Executors.newSingleThreadScheduledExecutor(withThreadNamePattern("auto-record"));
    private final ScheduledExecutorService autoFlushScheduler = Executors.newSingleThreadScheduledExecutor(withThreadNamePattern("auto-flush"));


    public void startAutoRecordAndFlush() {
        startAutoRecordAndFlush(getCommonProps().getDataRecorderAutoFlushInterval());
    }

    public void startAutoRecordAndFlush(long flushInterval) {
        if (state.recording.isAutoRecordingActive()) {
            log.info("Ignoring call to activate auto recording (with flush interval of [{}]), " +
                    "it is already active.", flushInterval);
            return;
        }

        state.recording.setAutomaticRecordingActiveOrFail();

        // TODO: Start thread and call record()
        log.info("Starting automatic recording.");

        long shortestInterval = getShortestInterval();

        autoRecordScheduler.scheduleAtFixedRate(() -> autoRecord(), 20, shortestInterval, TimeUnit.MILLISECONDS);
        log.info("Starting auto-record thread with interval of [{}] ms.", shortestInterval);
        autoFlushScheduler.scheduleAtFixedRate(() -> autoFlush(), 20, flushInterval, TimeUnit.MILLISECONDS);
        log.info("Starting auto-flush thread with interval of [{}] ms.", flushInterval);
    }

    private void autoRecord() {
        log.trace("Processing auto record request...");
        ExceptionUtil.suppressVoid(() -> autoRecordInternal(), "autoRecordInternal()");
        log.trace("Processing auto record request complete.");
    }

    private void autoRecordInternal() {
        state.recording.setAutomaticRecordingActiveOrFail();
        record();
    }

    private void autoFlush() {
        log.trace("Processing auto flush request...");
        ExceptionUtil.suppressVoid(() -> autoFlushInternal(), "autoFlushInternal()");
        log.trace("Processing auto flush request complete.");
    }

    private void autoFlushInternal() {
        state.recording.setAutomaticRecordingActiveOrFail();
        flush();
    }

    // Internal record/flush methods
    // ============================================================

    private void record() {
        boolean shouldExecute = periodic();

        if (!shouldExecute)
            return;

        recordInternal();
    }

    private void recordInternal() {

        log.trace("Processing record request...");
        state.iteration.markRecordingPhaseBegin();

        for (Entry<RecordableWrapper, DataRecordTable> entry : dataTableMap.entrySet()) {
            DataRecordTable table = entry.getValue();
            String name = table.getName();
            log.trace("Recording [{}]...", name);
            table.record();
            log.trace("Recording [{}] complete.", name);
        }

        state.iteration.markRecordingPhaseEnd();
        log.trace("Processing record request complete.");
    }

    private void flush() {
        boolean shouldExecute = periodic();

        if (!shouldExecute)
            return;

        // TODO: synchronize/wait for recording to finish

        flushInternal();
    }

    private void flushInternal() {
        log.trace("Processing flush request...");
        state.iteration.markFlushingPhaseBegin();

        if (!initializedOutputHandlers) {
            initializedOutputHandlers = true;
            outputHandlerList.forEach(DataRecordOutputHandler::init);
        }

        for (DataRecordTable table : dataTableMap.values()) {

            log.trace("Flushing table [{}]...");
            table.onBeforeFlush();
            List<DataRecordRow> rowsReadyToFlush = table.getRowsReadyToFlush();

            if (rowsReadyToFlush.isEmpty()) {
                log.warn("Requested to write data but no new rows to write. Recordable: [{}].", simpleName(table.getRecordable()));
                continue;
            }

            log.trace("Flushing table [{}]...");
            for (DataRecordOutputHandler outputHandler : outputHandlerList) {
                log.trace("Flushing output handler [{}]...", simpleName(outputHandler));

                outputHandler.flushTable(table, rowsReadyToFlush);

                log.trace("Flushing output handler [{}] complete.", simpleName(outputHandler));
            }

            table.onAfterFlush();
            log.trace("Flushing table [{}] complete.");
        }

        state.iteration.markFlushingPhaseEnd();
        log.trace("Processing flush request complete.");
    }

    // Helper methods
    // ============================================================

    /** @return true if we should continue with this cycle, false if we should exit */
    private boolean periodic() {

        if (state.initialization.isShutdownRequested()) {
            log.info("Shutting down...");
            shutdown();
            log.info("Shutting down complete.");
            return false;
        }

        // TODO: Should we move this below disabled? No sense in initializing if we're not gonna run
        if (state.initialization.isStartUpRequired()) {
            log.info("Starting up...");
            startup();
            log.info("Starting up complete.");
        }

        updateDisabledMode();

        if (state.disable.isDisabled()) {
            log.trace("Disabled.");
            return false;
        }

        log.trace("Enabled.");
        return true;
    }

    private void startup() {
        boolean fatalError = false;

        if (outputHandlerList.isEmpty())
            registerCsvOutputHandler();

        if (dataTableMap.isEmpty()) {
            state.disable.updateDisabledModeToActive(DISABLED_DUE_TO_NO_RECORDABLE_INSTANCES_REGISTERED);
            fatalError = true;
        }

        if (outputHandlerList.isEmpty()) {
            state.disable.updateDisabledModeToActive(DISABLED_DUE_TO_NO_OUTPUT_HANDLERS_REGISTERED);
            fatalError = true;
        }

        // TODO: this is where we would init output handers

        if (!fatalError)
            state.initialization.setIntializationPhase(RUNNING);
    }

    private void shutdown() {
        log.info("Flushing output handlers and shutting down...");
        // skip any checks ( flush() )and just shutdown
        flushInternal();
        state.initialization.setIntializationPhase(SHUTDOWN);
    }

    private void updateDisabledMode() {

        // Run these checks every time (updatable)

        // disable() and enable() also mutate this state
        state.disable.updateDisabledMode(DISABLED_DUE_TO_REAL_MATCH, getCommonProps().getRealMatch());
        state.disable.updateDisabledMode(DISABLED_BY_PROPERTIES, !getCommonProps().getDataRecorderEnabled());

    }

    // Getter methods
    // ============================================================

    private long getShortestInterval() {
        return getDataTableList().stream()
                .mapToLong(it -> it.getPeriod())
                .min()
                .orElse(getCommonProps().getDataRecorderDefaultRecordInterval());
    }

    private String getStateToString() {
        if (state.disable.isDisabled())
            return state.disable.getDisabledCauseString();

        if (!state.initialization.isRunning())
            return state.initialization.getInitializationPhase().toString();

        return state.iteration.getIterationPhase().toString();
    }

    private CommonProperties getCommonProps() {
        return RobotContext.getInstance().getCommonProps();
    }

    // Testing methods
    // ============================================================

    @VisibleForTesting
    List<Object> getRegisteredRecordables() {
        return Collections.unmodifiableList(dataTableMap.keySet().stream().map(it -> it.getInstance()).collect(Collectors.toList()));
    }

    @VisibleForTesting
    List<DataRecordTable> getDataTableList() {
        return Collections.unmodifiableList(new ArrayList<>(dataTableMap.values()));
    }

    @VisibleForTesting
    Set<DataRecordOutputHandler> getOutputHandlerList() {
        return Collections.unmodifiableSet(outputHandlerList);
    }
}
