package com.team2073.common.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;

public class JoystickPOVAndSensor extends MultiTrigger {
	public JoystickPOVAndSensor(Joystick controller, int pov, DigitalInput sensor) {
		super(new JoystickPOV(controller, pov), new Sensor(sensor));
	}
}
