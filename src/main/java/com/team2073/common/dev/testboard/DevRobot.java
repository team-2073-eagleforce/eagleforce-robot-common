package com.team2073.common.dev.testboard;

import com.team2073.common.dev.util.DevUtils;
import com.team2073.common.robot.AbstractRobotDelegate;

import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * A simple Robot to use for dev/testing. Use the run config: <b>PWRUP-5 [TestBoard]</b>
 * to deploy this onto a test board.
 *
 * @author Preston Briggs
 */
public class DevRobot extends AbstractRobotDelegate {

	@Override
	public void robotInit() {
		DevOperatorInterface.init();
	}

	@Override
	public void disabledInit() {
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void teleopInit() {
	}

	@Override
	public void testInit() {
	}

	@Override
	public void robotPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void testPeriodic() {
	}
}
