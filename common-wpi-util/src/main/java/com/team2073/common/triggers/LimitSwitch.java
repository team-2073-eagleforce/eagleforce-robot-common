package com.team2073.common.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class LimitSwitch extends Trigger {
	private DigitalInput sensor;
	
	public LimitSwitch(DigitalInput sensor) {
		this.sensor = sensor;
	}
	
	@Override
	public boolean get() {
//		TODO: CHANGE WIRING to reverse or leave decide later
		return !sensor.get();
	}

}