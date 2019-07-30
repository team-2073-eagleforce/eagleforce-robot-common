package com.team2073.common.controller;

import com.team2073.common.sim.ComponentType;
import com.team2073.common.trigger.MultiTrigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class EagleWheel implements UsbController{
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

	@Override
	public boolean getRawButton(int port) {
		return wheel.getRawButton(port);
	}

	@Override
	public double getRawAxis(int axis) {
		return wheel.getRawAxis(axis);
	}

	@Override
	public int getPOV() {
		return wheel.getPOV();
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.JOYSTICK;
	}

	@Override
	public int getPort() {
		return wheel.getPort();
	}


	@Override
	public String getName() {
		return EagleWheel.class.getName() + "_" + getPort();
	}
}
