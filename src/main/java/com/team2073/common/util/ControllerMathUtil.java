package com.team2073.common.util;

import edu.wpi.first.wpilibj.Joystick;

/**
 * @author pbriggs
 */
public class ControllerMathUtil {

    public static double angleOfJoystick(double xAxis, double yAxis) {
        if(((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 >= 215)
            return 215.;
        else if(((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 <= 70)
            return 70.;
        else
            return ((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35;
    }

    public static double angleOfJoystick(Joystick controller, int joystickXAxis, int joystickYAxis, boolean rightJoystick) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        if(rightJoystick)
            xAxis = xAxis * -1;
        return angleOfJoystick(xAxis, yAxis);
    }

    public static boolean isJoystickActive(Joystick controller, int joystickXAxis, int joystickYAxis, boolean rightJoystick) {
        if(rightJoystick)
            return isRightJoystickActive(controller, joystickXAxis, joystickYAxis);
        else
            return isLeftJoystickActive(controller, joystickXAxis, joystickYAxis);
    }

    public static boolean isLeftJoystickActive(double xAxis, double yAxis) {
        return Math.pow(xAxis, 2) + Math.pow(yAxis, 2) >= .5 && xAxis >= 0;
    }

    public static boolean isRightJoystickActive(double xAxis, double yAxis) {
        return Math.pow(xAxis, 2) + Math.pow(yAxis, 2) >= .5 && xAxis < 0;
    }

    public static boolean isLeftJoystickActive(Joystick controller, int joystickXAxis, int joystickYAxis) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        return isLeftJoystickActive(xAxis, yAxis);
    }

    public static boolean isRightJoystickActive(Joystick controller, int joystickXAxis, int joystickYAxis) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        return isRightJoystickActive(xAxis, yAxis);
    }

}
