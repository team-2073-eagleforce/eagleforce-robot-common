package com.team2073.common.robot;

import com.team2073.common.config.CommonProperties;
import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.event.RobotEventPublisher;
import com.team2073.common.periodic.OccasionalLoggingRunner;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.periodic.SmartDashboardAwareRunner;
import com.team2073.common.proploader.PropertyLoader;
import edu.wpi.first.wpilibj.IterativeRobotBase;

/**
 * An interface declaring all the methods a Robot might need. Extend {@link AbstractRobotDelegate}
 * instead of implementing this class directly if you don't want to define no-op implementations
 * of all the methods.
 *
 * @author Preston Briggs
 */
public interface RobotDelegate {

	/** See {@link IterativeRobotBase#robotInit()} */
	default void robotInit() {
	}

	/** See {@link IterativeRobotBase#disabledInit()} */
	default void disabledInit() {
	}

	/** See {@link IterativeRobotBase#autonomousInit()} */
	default void autonomousInit() {
	}

	/** See {@link IterativeRobotBase#teleopInit()} */
	default void teleopInit() {
	}

	/** See {@link IterativeRobotBase#testInit()} */
	default void testInit() {
	}

	/** See {@link IterativeRobotBase#robotPeriodic()} */
	default void robotPeriodic() {
	}

	/** See {@link IterativeRobotBase#disabledPeriodic()} */
	default void disabledPeriodic() {
	}

	/** See {@link IterativeRobotBase#autonomousPeriodic()} */
	default void autonomousPeriodic() {
	}

	/** See {@link IterativeRobotBase#teleopPeriodic()} */
	default void teleopPeriodic() {
	}

	/** See {@link IterativeRobotBase#testPeriodic()} */
	default void testPeriodic() {
	}
	
	default CommonProperties createCommonProperties() {
		return null;
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
	
	default PropertyLoader createPropertyLoader() {
		return null;
	}

	default void registerPeriodicInstance(PeriodicRunner periodicRunner) {

	}
	
	double getPeriod();
}