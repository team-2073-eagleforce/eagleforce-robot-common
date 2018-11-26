package com.team2073.common.datarecorder.model;

import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.team2073.common.datarecorder.model.DataRecorderStateMachine.InitializationPhase.*;
import static com.team2073.common.datarecorder.model.DataRecorderStateMachine.IterationPhase.*;
import static com.team2073.common.datarecorder.model.DataRecorderStateMachine.RecordingType.*;
import static com.team2073.common.util.ClassUtil.*;

/**
 * Manages the complex rules/coordination between multiple states of a {@link DataRecorder}.
 *
 * @author Preston Briggs
 */
public class DataRecorderStateMachine {

    public enum DisabledCause {
        DISABLED_DUE_TO_ERROR(false, 1, "Error occurred."),
        DISABLED_DUE_TO_REAL_MATCH(false, 2, "Real match detected through properties."),
        DISABLED_DUE_TO_NO_OUTPUT_HANDLERS_REGISTERED(false, 3, "No output handlers registered."),
        DISABLED_DUE_TO_NO_RECORDABLE_INSTANCES_REGISTERED(false, 4, "No recordable instances registered."),
        DISABLED_BY_PROPERTIES(true, 5, "Disabled property set to true."),
        DISABLED_MANUALLY(true, 6, "Manually disabled."),
        ;

        /** Whether once in this mode, we can ever return back to an enabled state. */
        private final boolean canEnabledFrom;

        /** Used for logging. */
        private final int displayPriority;

        /** The message to be logged the first time this DisabledCause is activated. */
        private final String logMsg;

        DisabledCause(boolean canEnabledFrom, int displayPriority, String logMsg) {
            this.canEnabledFrom = canEnabledFrom;
            this.logMsg = logMsg;
            this.displayPriority = displayPriority;
        }
    }

    public enum InitializationPhase {
        NEW,
        INITIALIZING,
        RUNNING,
        SHUTTING_DOWN,
        SHUTDOWN,
        FATAL_ERROR,
    }

    public enum IterationPhase {
        SLEEPING,
        RECORDING,
        FLUSHING,
    }

    public enum RecordingType {
        MANUAL,
        PERIODIC_RUNNER,
        AUTOMATIC,
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    public final InitializationPhaseMachine initialization = new InitializationPhaseMachine();
    public final IterationPhaseMachine iteration = new IterationPhaseMachine();
    public final DisabledCauseMachine disable = new DisabledCauseMachine();
    public final RecordingTypeMachine recording = new RecordingTypeMachine();

    public class InitializationPhaseMachine {

        private InitializationPhase initializationPhase = NEW;
        private boolean startedPreviously = false;

        public boolean isStartUpRequired() {
            return initializationPhase == INITIALIZING;
        }

        public boolean isRunning() {
            return initializationPhase == RUNNING;
        }

        public boolean isShutdownRequested() {
            return initializationPhase == SHUTTING_DOWN;
        }

        public boolean isShutdown() {
            return initializationPhase == SHUTDOWN;
        }

        public boolean startedPreviously() {
            return startedPreviously;
        }

        public InitializationPhase getInitializationPhase() {
            return initializationPhase;
        }

        public void requestStartUp() {
            initialization.initializationPhase = INITIALIZING;
            log.info("Startup requested.");
        }

        public void requestShutdown() {
            initialization.initializationPhase = SHUTTING_DOWN;
            log.info("Requesting shutdown.");
        }

        public void setIntializationPhase(InitializationPhase phase) {

            if (initializationPhase == FATAL_ERROR && phase != FATAL_ERROR)
                Throw.illegalArg("Cannot recover from initializationPhase [{}]. " +
                        "(attempted to set initializationPhase to [{}].", FATAL_ERROR, phase);

            switch (phase) {
                case NEW:
                    Throw.illegalState("Cannot set [{}] initializationPhase back to [{}] from state [{}].", simpleName(DataRecorder.class), phase, initializationPhase);
                    break;

                case INITIALIZING:
                    break;

                case RUNNING:
                    startedPreviously = true;
                    break;

                case SHUTDOWN:
                    if (initializationPhase != SHUTTING_DOWN)
                        Throw.illegalState("Cannot set [{}] initializationPhase to [{}] except from state [{}].", simpleName(DataRecorder.class), phase, SHUTTING_DOWN);
                    break;

                default:
                    EnumUtil.throwUnknownValueException(phase);
            }

            initializationPhase = phase;
        }
    }

    public class IterationPhaseMachine {

        // TODO: Change the rest of these to be AtomicBooleans
        private AtomicBoolean recording = new AtomicBoolean(false);
        private boolean flushing = false;

        public IterationPhase getIterationPhase() {
            if (flushing)
                return FLUSHING;
            else if (recording.get())
                return RECORDING;
            else
                return SLEEPING;
        }

        public void markRecordingPhaseBegin() {
            if (recording.get())
                Throw.illegalState("Attempted to start recording phase when we were already recording. " +
                                "Most likely a concurrency issue. Was the [{}] registered multiple times with the [{}]?",
                        simpleName(DataRecorder.class), simpleName(PeriodicRunner.class));

            recording.set(true);
        }

        public void markRecordingPhaseEnd() {
            if (!recording.get())
                Throw.illegalState("Attempted to stop recording phase when we had already stopped recording. " +
                                "Most likely a concurrency issue. Was the [{}] registered multiple times with the [{}]?",
                        simpleName(DataRecorder.class), simpleName(PeriodicRunner.class));

            recording.set(false);
        }


        public void markFlushingPhaseBegin() {
            if (flushing)
                Throw.illegalState("Attempted to start flushing phase when we were already flushing. " +
                                "Most likely a concurrency issue. Was the [{}] registered multiple times with the [{}]?",
                        simpleName(DataRecorder.class), simpleName(PeriodicRunner.class));

            flushing = true;
        }

        public void markFlushingPhaseEnd() {
            if (!flushing)
                Throw.illegalState("Attempted to start flushing phase when we were already flushing. " +
                                "Most likely a concurrency issue. Was the [{}] registered multiple times with the [{}]?",
                        simpleName(DataRecorder.class), simpleName(PeriodicRunner.class));

            flushing = false;
        }

    }

    public class DisabledCauseMachine {

        private SortedSet<DisabledCause> disabledCauseSet = new TreeSet<>(Comparator.comparingInt(it -> it.displayPriority));

        public boolean isDisabled() {
            return !disabledCauseSet.isEmpty();
        }

        public boolean isDisabledManually() {
            return disabledCauseSet.contains(DisabledCause.DISABLED_MANUALLY);
        }

        public boolean canEnableFromCurrentState() {
            return disabledCauseSet.stream().allMatch(it -> it.canEnabledFrom);
        }

        public String getDisabledCauseString() {
            return disabledCauseSet.stream().map(it -> it.toString()).collect(Collectors.joining(", "));
        }

        public void updateDisabledModeToActive(DisabledCause disabledCause) {
            updateDisabledMode(disabledCause, true);
        }

        public void updateDisabledModeToInactive(DisabledCause disabledCause) {
            updateDisabledMode(disabledCause, false);
        }

        public void updateDisabledMode(DisabledCause disabledCause, boolean active) {
            boolean prevDisabled = isDisabled();
            boolean currDisabled;

            if (active)
                logAndAdd(disabledCause);
            else
                logAndRemove(disabledCause);

            currDisabled = isDisabled();

            if (currDisabled && !canEnableFromCurrentState() && !initialization.startedPreviously())
                initialization.initializationPhase = FATAL_ERROR;

            if (prevDisabled && !currDisabled) {
                log.debug("All disabled causes removed, requesting startup.");
                initialization.requestStartUp();
            }
        }

        private void logAndAdd(DisabledCause disabledCause) {
            if (!disabledCauseSet.contains(disabledCause)) {

                if (initialization.startedPreviously()) {
                    log.info(disabledCause.logMsg + " No further recording will occur.");
                    initialization.requestShutdown();
                } else {
                    log.info(disabledCause.logMsg + " No recording will occur.");
                }
            }

            disabledCauseSet.add(disabledCause);
        }

        private void logAndRemove(DisabledCause disabledCause) {

            if (disabledCauseSet.contains(disabledCause)) {
                if (!disabledCause.canEnabledFrom) {
                    Throw.illegalArg("Cannot re-enable from state [{}].", disabledCause);
                } else {
                    disabledCauseSet.remove(disabledCause);
                    log.info("Removed disabled cause: " + disabledCause.logMsg);
                }
            }
        }
    }

    public class RecordingTypeMachine {

        private RecordingType recordingType;

        public boolean isManualRecordingActive() {
            return recordingType == MANUAL;
        }

        public boolean isPeriodicRecordingActive() {
            return recordingType == PERIODIC_RUNNER;
        }

        public boolean isAutoRecordingActive() {
            return recordingType == AUTOMATIC;
        }

        public void setManualRecordingActiveOrFail() {
            verifyCanSwitchTo(MANUAL);
            recordingType = MANUAL;
        }

        public void setPeriodicRecordingActiveOrFail() {
            verifyCanSwitchTo(PERIODIC_RUNNER);
            recordingType = PERIODIC_RUNNER;
        }

        public void setAutomaticRecordingActiveOrFail() {
            verifyCanSwitchTo(AUTOMATIC);
            recordingType = AUTOMATIC;
        }

        private void verifyCanSwitchTo(RecordingType newRecordingType) {
            if (recordingType != null && recordingType != newRecordingType)
                Throw.illegalState("Cannot switch to [{}] mode when [{}] mode already started.",
                        newRecordingType, recordingType);

        }

    }

}
