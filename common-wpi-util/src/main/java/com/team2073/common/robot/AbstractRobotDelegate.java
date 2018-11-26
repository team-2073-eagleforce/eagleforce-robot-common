package com.team2073.common.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.HALUtil;

/**
 * A simple implementation of {@link RobotDelegate} that provides a no-op
 * implementation of all the required methods (so you don't have to). Also fills
 * in the utility methods such as {@link RobotBase#isEnabled()} so they are
 * still available from subclasses.
 *
 * @author Preston Briggs
 */
public class AbstractRobotDelegate implements RobotDelegate {

	// Static
	// ============================================================

	/** See {@link IterativeRobot#isSimulation()} */
	static boolean isSimulation() {
		return !isReal();
	}

	/** See {@link IterativeRobot#isReal()} */
	static boolean isReal() {
		return HALUtil.getHALRuntimeType() == 0;
	}

	// Instance
	// ============================================================
	protected final DriverStation ds;

	public AbstractRobotDelegate() {
		ds = DriverStation.getInstance();
	}

	// IterativeRobot utility methods
	// ==========================================
	/** See {@link IterativeRobot#isDisabled()} */
	public final boolean isDisabled() {
		return ds.isDisabled();
	}

	/** See {@link IterativeRobot#isEnabled()} */
	public final boolean isEnabled() {
		return ds.isEnabled();
	}

	/** See {@link IterativeRobot#isAutonomous()} */
	public final boolean isAutonomous() {
		return ds.isAutonomous();
	}

	/** See {@link IterativeRobot#isTest()} */
	public final boolean isTest() {
		return ds.isTest();
	}

	/** See {@link IterativeRobot#isOperatorControl()} */
	public final boolean isOperatorControl() {
		return ds.isOperatorControl();
	}

	/** See {@link IterativeRobot#isNewDataAvailable()} */
	public final boolean isNewDataAvailable() {
		return ds.isNewControlData();
	}
}
