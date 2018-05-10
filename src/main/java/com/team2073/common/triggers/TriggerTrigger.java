package com.team2073.common.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class TriggerTrigger extends Trigger {
	private Joystick controller;
	private int axis;
	
	public TriggerTrigger(Joystick controller, int axis) {
		this.controller = controller;
		this.axis = axis;
	}
	
	@Override
	public boolean get() {
		return controller.getRawAxis(axis) > .1;
	}

}
