package com.team2073.common.controllers;

import com.team2073.common.triggers.MultiTrigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class EagleWheel {
	private Joystick wheel;
	private JoystickButton leftPaddle;
	private JoystickButton rightPaddle;
	private Trigger bothPaddles;

	public EagleWheel(int port) {
		this.wheel = new Joystick(port);
		leftPaddle = new JoystickButton(wheel, 1);
		rightPaddle = new JoystickButton(wheel, 3);
		bothPaddles = new MultiTrigger(leftPaddle, rightPaddle);
	}

	public Trigger getBothPaddles() {
		return bothPaddles;
	}

	public void setBothPaddles(Trigger bothPaddles) {
		this.bothPaddles = bothPaddles;
	}

	public JoystickButton getLeftPaddle() {
		return leftPaddle;
	}

	public JoystickButton getRightPaddle() {
		return rightPaddle;
	}
}
