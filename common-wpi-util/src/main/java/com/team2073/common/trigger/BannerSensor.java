package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class BannerSensor extends Trigger {
	private DigitalInput sensor;
	
	public BannerSensor(DigitalInput sensor) {
		this.sensor = sensor;
	}
	
	@Override
	public boolean getAsBoolean() {
		return sensor.get();
	}

}
