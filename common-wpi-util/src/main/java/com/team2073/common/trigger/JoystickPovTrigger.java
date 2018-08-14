package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class JoystickPovTrigger extends Trigger {
	private final Joystick controller;
	private final int pov;

	public JoystickPovTrigger(Joystick controller, int pov) {
		this.controller = controller;
		this.pov = pov;
	}

	@Override
	public boolean get() {
		return controller.getPOV() == pov;
	}
}
