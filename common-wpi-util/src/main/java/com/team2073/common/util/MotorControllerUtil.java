package com.team2073.common.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public abstract class MotorControllerUtil {

	private MotorControllerUtil() {}

	public static void setFollowerOf(BaseMotorController follower, BaseMotorController lead) {
		follower.set(ControlMode.Follower, lead.getDeviceID());
	}

}
