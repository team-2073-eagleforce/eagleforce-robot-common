package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.model.*;
import com.team2073.common.datarecorder.model.FieldMapping.BooleanFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.DoubleFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.LongFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.StringFieldMapping;
import com.team2073.common.datarecorder.output.DataRecordOutputHandler;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ReflectionUtil.PrimitiveType;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

import static com.team2073.common.datarecorder.DataRecorder.DataRecorderState.*;
import static com.team2073.common.datarecorder.DataRecorder.RecordingType.*;
import static com.team2073.common.util.ClassUtil.*;

/**
 * @author pbriggs
 */
public class DataRecorder {

    public enum DataRecorderState {
        NEW,
        REGISTERING,
        INITIALIZED,
        RECORDING,
        FLUSHING,
        SLEEPING,
        NO_OUTPUT_HANDLERS_REGISTERED,
        NO_RECORDABLE_INSTANCES_REGISTERED,
        DISABLED,
        DISABLED_DUE_TO_ERROR;
    }

    public enum RecordingType {
        AUTOMATIC,
        MANUAL
    }

    // TODO
    // -Add timestamp as a field mapping?
    // -Add synchronization on flushing (lock, copy over to a new list, unlock, then flush)
    // -Create pluggable outputs (allow multiple)
    // -Allow auto flushing every x iterations
    // -Add exception handling
    // -If no output handlers registered don't iterate
    // -Change DataRecordOutputHandler to interface
    // -Create "strategies" for resolving field names (snake case, etc.)
    // -Allow changing delimmiter on console and csv
    // -Add a getHeaders method on Registration (or instance actually?)
    //      -Two methods?, one returning String array, one accepting delimitters and new line
    //      -boolean to include timestamp as column
    //      -Related:
    //          -Allow customizing the timestamp format
    // -Allow disabling (or better yet, require enabling?)
    // -How can we be positive we are printing the column headers in the same order as row data?
    //      -I feel like the row data should have a reference to its column
    // -Test log/exception messages
    // -Make sure columns stay in correct order (might need to add an order attribute to @DataPoint
    // -Allow throwing an exception from DataRecordOutputHandler.init
    //      -What about flush tables? Or maybe wrap each table and stop trying on ones that fail? This should be an abstract impl of DataRecordOutputHandler
    // -Add a logging output handler

    public static final long DEFAULT_PERIOD = 20;

    private Logger log = LoggerFactory.getLogger(getClass());
    private DataRecorderState state = DataRecorderState.NEW;
    private RecordingType recordingType;
    private boolean initializedOutputHandlers;
    private Map<RecordableRegistration, DataRecordTable> dataRecordMap = new HashMap<>();
    private List<DataRecordOutputHandler> outputHandlerList = new ArrayList<>();

    public void register(Recordable recordable) {
        register(recordable, DEFAULT_PERIOD);
    }

    public void register(Recordable recordable, long period) {
        String recordableName = simpleName(recordable);
        log.info("Registering Recordable [{}].", recordableName);
        if (started())
            Throw.illegalState("Cannot register any more instances when runner has already started. DataRecorderState: [%s].", state);

        state = REGISTERING;

        // If this recordable doesn't already exist, add it to the map
        RecordableRegistration newRegistration = new RecordableRegistration(recordable, period);
        createFieldMappings(newRegistration);
        DataRecordTable dataRecordTable = dataRecordMap.get(newRegistration);
        if (dataRecordTable == null) {
            log.debug("Registering Recordable [{}] complete.", recordableName);
            dataRecordMap.put(newRegistration, new DataRecordTable());
        } else {
            // This recordable has already been registered. Check if the interval is different between the registrations
            // and fail if so
            Optional<RecordableRegistration> existingRegistration = dataRecordMap.keySet().stream().filter(e -> e.equals(newRegistration)).findFirst();
            if (existingRegistration.isPresent() && existingRegistration.get().getPeriod() != newRegistration.getPeriod()) {
                Throw.illegalState("Cannot register the same Recordable instance multiple times with different periods. " +
                        "Recordable: [%s]. Periods: [%s] and [%s].", recordableName,
                        existingRegistration.get().getPeriod(), newRegistration.getPeriod());
            } else {
                log.warn("Requested to register the same Recordable instance [{}] multiple times. Ignoring...",
                        recordableName);
            }
        }

        // Could do the following instead if we don't care about failing fast/logging
//        DataRecordTable table = dataRecordMap.computeIfAbsent(newRegistration, k -> new DataRecordTable());
    }

    private void createFieldMappings(RecordableRegistration registration) {
        Recordable instance = registration.getInstance();
        String recordableName = simpleName(instance);
        List<FieldMapping> fieldMappingList = registration.getFieldMappingList();

        log.info("Creating field mapping for Recordable [{}]...", recordableName);

        List<Field> fields = ReflectionUtil.getInheritedPrivateFields(instance.getClass());
        String transientAnnotationName = "@" + Transient.class.getSimpleName();
        for (Field field : fields) {
            final String fieldName = field.getName();
            final String logPrefix = recordableName + "." + fieldName + ":";

            if (field.getAnnotation(Transient.class) != null) {
                log.debug("{} Skipping field marked with [{}].", logPrefix, transientAnnotationName);
                continue;
            }

            log.debug("{} Registering field.", logPrefix);
            String dataPointName = fieldName;
            DataPoint dataPointAnnon = field.getAnnotation(DataPoint.class);
            if (dataPointAnnon != null && !dataPointAnnon.name().equals(DataPoint.NULL)) {
                dataPointName = dataPointAnnon.name();
                log.debug("{} Using custom field name [{}].", logPrefix, dataPointName);
            }

            // TODO try to get the getter instead

            Class<?> type = field.getType();
            if (!ReflectionUtil.isPrimitiveOrWrapper(type)) {
                Throw.illegalState("[%s] fields must be either primitive or primitive wrapper types. " +
                                "Found field [%s] of type [%s] in class [%s]. To ignore a field annotate it with [%s].",
                        Recordable.class.getSimpleName(), fieldName, type.getSimpleName(), recordableName,
                        transientAnnotationName);
            }

            field.setAccessible(true);

            PrimitiveType primitiveType = ReflectionUtil.getPrimitiveType(field);

            if (primitiveType == null) {
                Throw.illegalState("Could not determine primitive type from field [%s] in class [%s].", fieldName,
                        recordableName);
            }

            switch (primitiveType.group) {
                case TEXT:
                    log.debug("{} Creating String field mapping.", logPrefix);
                    fieldMappingList.add(new StringFieldMapping(field, dataPointName, instance));
                    break;

                case DIGIT:
                    log.debug("{} Creating Long field mapping.", logPrefix);
                    fieldMappingList.add(new LongFieldMapping(field, dataPointName, instance));
                    break;

                case DECIMAL:
                    log.debug("{} Creating Double field mapping.", logPrefix);
                    fieldMappingList.add(new DoubleFieldMapping(field, dataPointName, instance));
                    break;

                case BOOLEAN:
                    log.debug("{} Creating Boolean field mapping.", logPrefix);
                    fieldMappingList.add(new BooleanFieldMapping(field, dataPointName, instance));
                    break;

                default:
                    EnumUtil.throwUnknownValueException(primitiveType.group);
            }

        }
        log.debug("Creating field mapping for Recordable [{}] complete.", recordableName);
    }

    public void registerOutputHandler(DataRecordOutputHandler handler) {
        log.info("Registered output handler [{}].", simpleName(handler));
        if (started())
            Throw.illegalState("Cannot register any more output handlers when runner has already started. DataRecorderState: [%s].", state);

        state = REGISTERING;
        outputHandlerList.add(handler);
    }

    public void startRecording() {
        if (disabled())
            return;

        if (manualRecording())
            Throw.illegalState("Attempted to start auto recording when manual recording has already started.");

        recordingType = AUTOMATIC;
        init();
        // TODO: Start thread and call recordInternal()
        log.info("Starting automatic recording.");
        throw new NotYetImplementedException();
    }

    /** Call this method periodically. This will poll all the instances and record their data. Alternatively
     * call {@link #startRecording()} one time to handle this automatically in a different thread. */
    public void record() {
        if (disabled())
            return;

        if (autoRecording())
            Throw.illegalState("Attempted to manual record when auto recording has already started.");

        recordingType = MANUAL;
        init();
        state = RECORDING;
        log.trace("Processing manual recording request...");
        recordInternal();
        log.trace("Processing manual recording request complete.");
        state = SLEEPING;
    }

    public void flush() {
        if (disabled())
            return;

        if (!initializedOutputHandlers) {
            initializedOutputHandlers = true;
            outputHandlerList.forEach(DataRecordOutputHandler::init);
        }

        // TODO: synchronize/wait for recording to finish
        state = FLUSHING;
        log.trace("Processing flush request...");
        for (DataRecordOutputHandler outputHandler : outputHandlerList) {
            log.trace("Flushing output handler [{}]...", simpleName(outputHandler));
            outputHandler.flushAllTables(dataRecordMap);
            log.trace("Flushing output handler [{}] complete.", simpleName(outputHandler));
        }
        log.trace("Processing flush request complete.");
        state = SLEEPING;
    }

    private void init() {
        if (started())
            return;

        if (dataRecordMap.isEmpty()) {
            state = NO_RECORDABLE_INSTANCES_REGISTERED;
            log.info("No recordable instances registered. No recording will occur...");
            return;
        }

        if (outputHandlerList.isEmpty()) {
            state = NO_OUTPUT_HANDLERS_REGISTERED;
            log.info("No output handlers registered. No recording will occur...");
            return;
        }

        state = INITIALIZED;
    }

    private void recordInternal() {
        LocalDateTime timeStamp = LocalDateTime.now();
        Set<RecordableRegistration> tableList = dataRecordMap.keySet();
        for (Entry<RecordableRegistration, DataRecordTable> entry : dataRecordMap.entrySet()) {
            RecordableRegistration recordable = entry.getKey();
            DataRecordTable table = entry.getValue();

            recordable.getInstance().onBeforeRecord();

            DataRecordRow row = new DataRecordRow();
            table.put(timeStamp, row);
            for (FieldMapping mapping : recordable.getFieldMappingList()) {
                row.add(new DataRecord<>(mapping.getDataPointName(), mapping.getFieldValue(), mapping));
            }

            recordable.getInstance().onAfterRecord();
        }
    }

    private boolean started() {
        return state != NEW && state != REGISTERING;
    }

    private boolean autoRecording() {
        return recordingType == AUTOMATIC;
    }

    private boolean manualRecording() {
        return recordingType == MANUAL;
    }

    private boolean disabled() {
        return state == NO_OUTPUT_HANDLERS_REGISTERED || state == DISABLED || state == DISABLED_DUE_TO_ERROR;
    }

}
