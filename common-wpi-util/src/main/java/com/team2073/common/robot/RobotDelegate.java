package com.team2073.common.robot;

import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.event.RobotEventPublisher;
import com.team2073.common.periodic.OccasionalLoggingRunner;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.periodic.SmartDashboardAwareRunner;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * An interface declaring all the methods a Robot might need. Extend {@link AbstractRobotDelegate}
 * instead of implementing this class directly if you don't want to define no-op implementations
 * of all the methods.
 *
 * @author Preston Briggs
 */
public interface RobotDelegate {

	/** See {@link IterativeRobot#robotInit()} */
	default void robotInit() {
	}

	/** See {@link IterativeRobot#disabledInit()} */
	default void disabledInit() {
	}

	/** See {@link IterativeRobot#autonomousInit()} */
	default void autonomousInit() {
	}

	/** See {@link IterativeRobot#teleopInit()} */
	default void teleopInit() {
	}

	/** See {@link IterativeRobot#testInit()} */
	default void testInit() {
	}

	/** See {@link IterativeRobot#robotPeriodic()} */
	default void robotPeriodic() {
	}

	/** See {@link IterativeRobot#disabledPeriodic()} */
	default void disabledPeriodic() {
	}

	/** See {@link IterativeRobot#autonomousPeriodic()} */
	default void autonomousPeriodic() {
	}

	/** See {@link IterativeRobot#teleopPeriodic()} */
	default void teleopPeriodic() {
	}

	/** See {@link IterativeRobot#testPeriodic()} */
	default void testPeriodic() {
	}

	/** See {@link IterativeRobot#free()} */
	default void free() {
	}

	default PeriodicRunner createPeriodicRunner() {
		return null;
	}

	default SmartDashboardAwareRunner createSmartDashboardRunner() {
		return null;
	}

	default OccasionalLoggingRunner createLoggingRunner() {
		return null;
	}

	default RobotEventPublisher createEventPublisher() {
		return null;
	}

	default DataRecorder createDataRecorder() {
		return null;
	}

	default void registerPeriodicInstance(PeriodicRunner periodicRunner) {

	}
}