package com.team2073.common.controller;

import com.team2073.common.util.ControllerUtil;
import edu.wpi.first.wpilibj.Joystick;

public class NamedJoystick extends Joystick {

	public NamedJoystick(String name) {
		super(ControllerUtil.findJoystickPortByName(name));
	}

}
