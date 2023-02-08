package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class LimitSwitchTrigger extends Trigger {

	private DigitalInput sensor;
	
	public LimitSwitchTrigger(DigitalInput sensor) {
		super(() -> !sensor.get());
		this.sensor = sensor;
	}
	
	@Override
	public boolean getAsBoolean() {
//		TODO: CHANGE WIRING to reverse or leave decide later
		return !sensor.get();
	}

}
