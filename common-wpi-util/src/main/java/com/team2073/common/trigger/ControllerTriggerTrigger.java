package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ControllerTriggerTrigger extends Trigger {

	private Joystick controller;
	private int axis;
	
	public ControllerTriggerTrigger(Joystick controller, int axis) {
		this.controller = controller;
		this.axis = axis;
	}
	
	@Override
	public boolean getAsBoolean() {
		return Math.abs(controller.getRawAxis(axis)) > .1;
	}

}
