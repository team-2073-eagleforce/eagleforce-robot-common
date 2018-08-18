package com.team2073.common.util;

import com.team2073.common.assertion.Assert;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author pbriggs
 */
public abstract class ControllerUtil {

    public static int findJoystickPortByName(String name) {
        Assert.assertNotNull(name, "name");
        for (int port = 0; port < DriverStation.kJoystickPorts; port++) {
            if (name.equals(DriverStation.getInstance().getJoystickName(port))) {
                return port;
            }
        }
        throw new IllegalArgumentException(String.format("Could not find joystick of name [%s]", name));
    }

}
