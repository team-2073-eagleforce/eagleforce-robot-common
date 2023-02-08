package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class HallEffectTrigger extends Trigger {

	private DigitalInput sensor;
	
	public HallEffectTrigger(DigitalInput sensor) {
		super(() -> !sensor.get());
		this.sensor = sensor;
	}
	
	@Override
	public boolean getAsBoolean() {
		return !sensor.get();
	}

}
