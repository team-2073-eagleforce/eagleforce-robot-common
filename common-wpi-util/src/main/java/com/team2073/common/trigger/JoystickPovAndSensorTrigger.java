package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;

public class JoystickPovAndSensorTrigger extends MultiTrigger {

	public JoystickPovAndSensorTrigger(Joystick controller, int pov, DigitalInput sensor) {
		super(new JoystickPovTrigger(controller, pov), new SensorTrigger(sensor));
	}

}
