package com.team2073.common.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;

public abstract class AbstractTwoSpeedDriveSubsystem extends AbstractEagledriveSubsystem {
	protected final Solenoid solenoid1;
	protected final Solenoid solenoid2;
	
	public AbstractTwoSpeedDriveSubsystem(
			TalonSRX leftMotor, TalonSRX rightMotor,
			TalonSRX leftMotorSlave, TalonSRX rightMotorSlave,
			Solenoid solenoid1, Solenoid solenoid2) {
		super(leftMotor, rightMotor, leftMotorSlave, rightMotorSlave);
		this.solenoid1 = solenoid1;
		this.solenoid2 = solenoid2;
	}

	public void shiftHighGear() {
		solenoid1.set(false);//TODO: rename misleading shiftHighGear/shiftLowGear names
		solenoid2.set(true);
	}

	public void shiftLowGear() {
		solenoid1.set(true);
		solenoid2.set(false);
	}
}
