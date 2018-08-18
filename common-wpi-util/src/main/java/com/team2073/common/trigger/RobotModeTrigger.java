package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.RobotState;
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
		return checkEnabled ? mode.isEnabled() : !mode.isEnabled();
	}

	public enum RobotMode {
		AUTONOMOUS {
			@Override
			public boolean isEnabled() {
				return RobotState.isAutonomous();
			}
		},
		TELEOP {
			@Override
			public boolean isEnabled() {
				return RobotState.isOperatorControl();
			}
		},
		TEST {
			@Override
			public boolean isEnabled() {
				return RobotState.isTest();
			}
		};

		public abstract boolean isEnabled();
	}
}
