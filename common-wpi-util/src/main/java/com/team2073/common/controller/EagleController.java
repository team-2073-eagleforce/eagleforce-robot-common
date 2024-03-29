package com.team2073.common.controller;

import com.team2073.common.sim.ComponentType;
import com.team2073.common.trigger.ControllerTriggerTrigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
//import edu.wpi.first.wpilibj.buttons.POVButton;
//import edu.wpi.first.wpilibj.buttons.Trigger;

public class EagleController implements UsbController {

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
        leftDPad = new POVButton(controller, 270);
        upDPad = new POVButton(controller, 0);
        rightDPad = new POVButton(controller, 90);
        downDPad = new POVButton(controller, 180);
        leftTrigger = new ControllerTriggerTrigger(controller, 2);
        rightTrigger = new ControllerTriggerTrigger(controller, 3);
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

    @Override
    public boolean getRawButton(int port) {
        return controller.getRawButton(port);
    }

    @Override
    public double getRawAxis(int axis) {
        return controller.getRawAxis(axis);
    }

    @Override
    public int getPOV() {
        return controller.getPOV();
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.JOYSTICK;
    }

    @Override
    public int getPort() {
        return controller.getPort();
    }

    @Override
    public String getName() {
        return EagleController.class.getName() + "_" + getPort();
    }
}
