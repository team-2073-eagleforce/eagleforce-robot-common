package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class SensorTrigger extends Trigger {

	private final DigitalInput sensor;

	public SensorTrigger(DigitalInput sensor) {
		super(() -> sensor.get());
		this.sensor = sensor;
	}

	@Override
	public boolean getAsBoolean() {
		return sensor.get();
	}
}
