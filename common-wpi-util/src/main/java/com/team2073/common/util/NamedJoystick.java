package com.team2073.common.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class NamedJoystick extends Joystick {
	public NamedJoystick(String name) {
		super(findJoystickPortByName(name));
	}
	
	private static int findJoystickPortByName(String name) {
		for (int port = 0; port < DriverStation.kJoystickPorts; port++) {
			if (name.equals(DriverStation.getInstance().getJoystickName(port))) {
				return port;
			}
		}
		throw new IllegalArgumentException(String.format("Could not find joystick of name [%s]", name));
	}
}
