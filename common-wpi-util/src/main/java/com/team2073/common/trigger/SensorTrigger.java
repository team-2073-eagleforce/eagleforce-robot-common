package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class SensorTrigger extends Trigger {

	private final DigitalInput sensor;

	public SensorTrigger(DigitalInput sensor) {
		this.sensor = sensor;
	}

	@Override
	public boolean get() {
		return sensor.get();
	}
}
