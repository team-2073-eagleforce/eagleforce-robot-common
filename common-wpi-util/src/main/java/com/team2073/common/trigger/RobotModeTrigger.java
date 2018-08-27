package com.team2073.common.trigger;

import com.team2073.common.robot.DetailedRobotState.RobotMode;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class RobotModeTrigger extends Trigger {

	private final RobotMode mode;
	private final boolean checkEnabled;

	public RobotModeTrigger(RobotMode mode, boolean checkEnabled) {
		this.mode = mode;
		this.checkEnabled = checkEnabled;
	}

	public RobotModeTrigger(RobotMode mode) {
		this(mode, true);
	}

	@Override
	public boolean get() {
		return checkEnabled ? mode.isCurrentState() : !mode.isCurrentState();
	}
}
