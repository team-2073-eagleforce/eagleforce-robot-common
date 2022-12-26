package com.team2073.common.robot;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.RobotBase;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * A simple implementation of {@link RobotDelegate} that provides the
 * utility methods such as {@link RobotBase#isEnabled()} so they are
 * still available from subclasses.
 *
 * @author Preston Briggs
 */
public class AbstractRobotDelegate implements RobotDelegate{

	// Static
	// ============================================================

//	/** See {@link IterativeRobotBase#isSimulation()} */
//	static boolean isSimulation() {
//		return !isReal();
//	}
//
//	/** See {@link IterativeRobotBase#isReal()} */
//	static boolean isReal() {
//		return HALUtil.getHALRuntimeType() == 0;
//	}

	// Instance
	// ============================================================

	private final double period;

	public AbstractRobotDelegate() {
		this(.01);
	}
	
	public AbstractRobotDelegate(double period) {
		this.period = period;

	}

	// IterativeRobot utility methods
	// ==========================================
	/** See {@link IterativeRobotBase#isDisabled()} */
	public final boolean isDisabled() {
		return DriverStation.isDisabled();
	}

	/** See {@link IterativeRobotBase#isEnabled()} */
	public final boolean isEnabled() {
		return DriverStation.isEnabled();
	}

	/** See {@link IterativeRobotBase#isAutonomous()} */
	public final boolean isAutonomous() {
		return DriverStation.isAutonomous();
	}

	/** See {@link IterativeRobotBase#isTest()} */
	public final boolean isTest() {
		return DriverStation.isTest();
	}

//	/** See {@link IterativeRobotBase#isOperatorControl()} */
//	public final boolean isOperatorControl() {
//		return ds.isOperatorControl();
//	}
//
//	/** See {@link IterativeRobotBase#isNewDataAvailable()} */
//	public final boolean isNewDataAvailable() {
//		return ds.isNewControlData();
//	}
	
	@Override
	public double getPeriod() {
		return period;
	}
}
