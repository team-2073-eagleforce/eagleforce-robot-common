package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class ControllerTriggerTrigger extends Trigger {

	private Joystick controller;
	private int axis;
	
	public ControllerTriggerTrigger(Joystick controller, int axis) {
		this.controller = controller;
		this.axis = axis;
	}
	
	@Override
	public boolean get() {
		return controller.getRawAxis(axis) > .1;
	}

}