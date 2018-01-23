package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class JoystickPOV extends Trigger {
	private final Joystick controller;
	private final int pov;

	public JoystickPOV(Joystick controller, int pov) {
		this.controller = controller;
		this.pov = pov;
	}

	@Override
	public boolean get() {
		return controller.getPOV() == pov;
	}
}
