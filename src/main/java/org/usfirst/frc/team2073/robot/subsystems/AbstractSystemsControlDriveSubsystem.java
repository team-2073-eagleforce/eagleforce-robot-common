package org.usfirst.frc.team2073.robot.subsystems;

import org.usfirst.frc.team2073.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public abstract class AbstractSystemsControlDriveSubsystem extends AbstractTwoSpeedDriveSubsystem {
	
	ADXRS450_Gyro gyro = RobotMap.getGyro();

	public AbstractSystemsControlDriveSubsystem() {
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
