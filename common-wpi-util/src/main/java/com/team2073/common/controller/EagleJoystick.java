package com.team2073.common.controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class EagleJoystick {

	private Joystick joystick;
	private JoystickButton power1;
	private JoystickButton power2;
	private JoystickButton power3;
	private JoystickButton power4;
	private JoystickButton power5;

	public EagleJoystick(int port) {
		this.joystick = new Joystick(port);
		power1 = new JoystickButton(joystick, 1);
		power2 = new JoystickButton(joystick, 2);
		power3 = new JoystickButton(joystick, 3);
		power4 = new JoystickButton(joystick, 4);
		power5 = new JoystickButton(joystick, 5);
	}

	public JoystickButton getPower1() {
		return power1;
	}

	public JoystickButton getPower4() {
		return power4;
	}

	public JoystickButton getPower3() {
		return power3;
	}

	public JoystickButton getPower2() {
		return power2;
	}

	public JoystickButton getPower5() {
		return power5;
	}
}
