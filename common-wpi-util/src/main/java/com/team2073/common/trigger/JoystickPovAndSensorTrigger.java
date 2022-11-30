package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.POVButton;

public class JoystickPovAndSensorTrigger extends MultiTrigger {

	public JoystickPovAndSensorTrigger(Joystick controller, int pov, DigitalInput sensor) {
		super(new POVButton(controller, pov), new SensorTrigger(sensor));
	}

}
