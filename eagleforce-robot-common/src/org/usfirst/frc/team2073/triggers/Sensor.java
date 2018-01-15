package org.usfirst.frc.team2073.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class Sensor extends Trigger {
	private final DigitalInput sensor;

	public Sensor(DigitalInput sensor) {
		this.sensor = sensor;
	}

	@Override
	public boolean get() {
		return sensor.get();
	}
}
