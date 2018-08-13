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
//		DevUtils.YOUGOBOOMNOW();
	}

	@Override
	public void disabledInit() {
//		DevUtils.YOUGOBOOMNOW();
	}

	@Override
	public void autonomousInit() {
//		DevUtils.YOUGOBOOMNOW();
	}

	@Override
	public void teleopInit() {
		DevUtils.YOUGOBOOMNOW();
	}

	@Override
	public void testInit() {
//		DevUtils.YOUGOBOOMNOW();
	}

	@Override
	public void robotPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void disabledPeriodic() {
//		DevUtils.YOUGOBOOM();
	}

	@Override
	public void autonomousPeriodic() {
		DevUtils.YOUGOBOOM();
	}

	@Override
	public void teleopPeriodic() {
//		DevUtils.YOUGOBOOM();
	}

	@Override
	public void testPeriodic() {
//		DevUtils.YOUGOBOOM();
	}
}
