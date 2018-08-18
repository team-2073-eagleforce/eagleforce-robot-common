package com.team2073.common.dev.testboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * A simple RobotMap to be used for dev/testing.
 *
 * @author Preston Briggs
 */
public class RobotMap {
	
	private static TalonSRX talon = new TalonSRX(0);
	
	public static DevMotorSubsystem motorSubsystem = new DevMotorSubsystem(talon);

}
