package com.team2073.common.dev.testboard;

import com.team2073.common.robot.RobotDelegate;
import com.team2073.common.util.ExceptionUtil;

import edu.wpi.first.wpilibj.TimedRobot;

public class DevRobotDelegator extends TimedRobot {
	
	private RobotDelegate robot = new DevRobot();

	@Override
	public void robotInit() {
		// Don't wrap in exception handling, handled by rebooting
		robot.robotInit();
	}

	@Override
	public void disabledInit() {
		ExceptionUtil.suppressVoid(robot::disabledInit, "robot::disabledInit");
	}

	@Override
	public void autonomousInit() {
		ExceptionUtil.suppressVoid(robot::autonomousInit, "robot::autonomousInit");
	}

	@Override
	public void teleopInit() {
		ExceptionUtil.suppressVoid(robot::teleopInit, "robot::teleopInit");
	}

	@Override
	public void testInit() {
		ExceptionUtil.suppressVoid(robot::testInit, "robot::testInit");
	}

	@Override
	public void robotPeriodic() {
		ExceptionUtil.suppressVoid(robot::robotPeriodic, "robot::robotPeriodic");
	}

	@Override
	public void disabledPeriodic() {
		ExceptionUtil.suppressVoid(robot::disabledPeriodic, "robot::disabledPeriodic");
	}

	@Override
	public void autonomousPeriodic() {
		ExceptionUtil.suppressVoid(robot::autonomousPeriodic, "robot::autonomousPeriodic");
	}

	@Override
	public void teleopPeriodic() {
		ExceptionUtil.suppressVoid(robot::teleopPeriodic, "robot::teleopPeriodic");
	}

	@Override
	public void testPeriodic() {
		ExceptionUtil.suppressVoid(robot::testPeriodic, "robot::testPeriodic");
	}
}
