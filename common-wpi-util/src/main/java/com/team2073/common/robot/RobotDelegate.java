package com.team2073.common.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * An interface declaring all the methods a Robot might need. Extend {@link AbstractRobotDelegate}
 * instead of implementing this class directly if you don't want to define no-op implementations
 * of all the methods.
 *
 * @author Preston Briggs
 */
public interface RobotDelegate {

	/** @see IterativeRobot#robotInit() */
	void robotInit();

	/** @see IterativeRobot#disabledInit() */
	void disabledInit();

	/** @see IterativeRobot#autonomousInit() */
	void autonomousInit();

	/** @see IterativeRobot#teleopInit() */
	void teleopInit();

	/** @see IterativeRobot#testInit() */
	void testInit();

	/** @see IterativeRobot#robotPeriodic() */
	void robotPeriodic();

	/** @see IterativeRobot#disabledPeriodic() */
	void disabledPeriodic();

	/** @see IterativeRobot#autonomousPeriodic() */
	void autonomousPeriodic();

	/** @see IterativeRobot#teleopPeriodic() */
	void teleopPeriodic();

	/** @see IterativeRobot#testPeriodic() */
	void testPeriodic();

	/** @see IterativeRobot#free() */
	void free();

}