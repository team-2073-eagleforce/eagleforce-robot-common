package com.team2073.common.controller;

import com.team2073.common.sim.ComponentType;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class EagleJoystick implements UsbController{

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

	@Override
	public boolean getRawButton(int port) {
		return joystick.getRawButton(port);
	}

	@Override
	public double getRawAxis(int axis) {
		return joystick.getRawAxis(axis);
	}

	@Override
	public int getPOV() {
		return joystick.getPOV();
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.JOYSTICK;
	}

	@Override
	public int getPort() {
		return joystick.getPort();
	}

	@Override
	public String getName() {
		return EagleJoystick.class.getName() + "_" + getPort();
	}
}
