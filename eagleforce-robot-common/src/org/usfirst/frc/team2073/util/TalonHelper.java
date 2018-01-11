package org.usfirst.frc.team2073.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonHelper {
	public static void setFollowerOf(TalonSRX follower, TalonSRX talon) {
		follower.set(ControlMode.Follower, talon.getDeviceID());;
	}
}
