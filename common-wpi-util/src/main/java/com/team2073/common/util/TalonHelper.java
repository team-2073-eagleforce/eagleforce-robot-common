package com.team2073.common.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonHelper {
	public static void setFollowerOf(TalonSRX follower, TalonSRX talon) {
		follower.set(ControlMode.Follower, talon.getDeviceID());;
	}
}
