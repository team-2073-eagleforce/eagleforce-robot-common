package com.team2073.common.controller;

import com.team2073.common.trigger.DPadTrigger;
import com.team2073.common.trigger.TriggerTrigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class EagleController {

	private Joystick controller;

	private Trigger leftDPad;
	private Trigger upDPad;
	private Trigger rightDPad;
	private Trigger downDPad;
	private Trigger leftTrigger;
	private Trigger rightTrigger;

	private JoystickButton a;
	private JoystickButton b;
	private JoystickButton y;
	private JoystickButton x;
	private JoystickButton start;
	private JoystickButton back;
	private JoystickButton rb;
	private JoystickButton lb;

	public EagleController(int port) {
		controller = new Joystick(port);
		leftDPad = new DPadTrigger(controller, 270);
		upDPad = new DPadTrigger(controller, 0);
		rightDPad = new DPadTrigger(controller, 90);
		downDPad = new DPadTrigger(controller, 180);
		leftTrigger = new TriggerTrigger(controller, 2);
		rightTrigger = new TriggerTrigger(controller, 3);

		a = new JoystickButton(controller, 1);
		b = new JoystickButton(controller, 2);
		y = new JoystickButton(controller, 4);
		x = new JoystickButton(controller, 3);
		start = new JoystickButton(controller, 8);
		back = new JoystickButton(controller, 7);
		rb = new JoystickButton(controller, 6);
		lb = new JoystickButton(controller, 5);
	}

	public Trigger getLeftDPad() {
		return leftDPad;
	}

	public Trigger getUpDPad() {
		return upDPad;
	}

	public Trigger getRightDPad() {
		return rightDPad;
	}

	public Trigger getDownDPad() {
		return downDPad;
	}

	public Trigger getLeftTrigger() {
		return leftTrigger;
	}

	public Trigger getRightTrigger() {
		return rightTrigger;
	}

	public JoystickButton getA() {
		return a;
	}

	public JoystickButton getB() {
		return b;
	}

	public JoystickButton getY() {
		return y;
	}

	public JoystickButton getX() {
		return x;
	}

	public JoystickButton getStart() {
		return start;
	}

	public JoystickButton getBack() {
		return back;
	}

	public JoystickButton getRb() {
		return rb;
	}

	public JoystickButton getLb() {
		return lb;
	}
}
