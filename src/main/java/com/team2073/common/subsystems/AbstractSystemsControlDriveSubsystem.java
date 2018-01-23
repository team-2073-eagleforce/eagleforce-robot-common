package com.team2073.common.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;

public abstract class AbstractSystemsControlDriveSubsystem extends AbstractTwoSpeedDriveSubsystem {
	private final ADXRS450_Gyro gyro;
	
	public AbstractSystemsControlDriveSubsystem(
			TalonSRX leftMotor, TalonSRX rightMotor,
			TalonSRX leftMotorSlave, TalonSRX rightMotorSlave,
			Solenoid solenoid1, Solenoid solenoid2,
			ADXRS450_Gyro gyro) {
		super(leftMotor, rightMotor, leftMotorSlave, rightMotorSlave, solenoid1, solenoid2);
		this.gyro = gyro;
		configEncoders();
		enableBrakeMode();
	}

	public void stopBrakeMode() {
		leftMotor.setNeutralMode(NeutralMode.Coast);
		rightMotor.setNeutralMode(NeutralMode.Coast);
	}

	public void enableBrakeMode() {
		leftMotor.setNeutralMode(NeutralMode.Brake);
		rightMotor.setNeutralMode(NeutralMode.Brake);
	}

	public void configEncoders() {
		leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 5);
		rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 5);
		// leftMotor.configEncoderCodesPerRev(1024);
		// rightMotor.configEncoderCodesPerRev(1024);
	}

	public double getGyroAngle() {
		return gyro.getAngle();
	}
}
