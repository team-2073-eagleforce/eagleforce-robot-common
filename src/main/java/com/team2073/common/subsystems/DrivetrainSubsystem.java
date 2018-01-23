package com.team2073.common.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.commands.drive.DriveWithJoystickCommand;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public abstract class DrivetrainSubsystem extends AbstractMotionProfileDriveSubsystem {
	private final Joystick joystick;
	private final Joystick wheel;
	
	public DrivetrainSubsystem(
			TalonSRX leftMotor, TalonSRX rightMotor,
			TalonSRX leftMotorSlave, TalonSRX rightMotorSlave,
			Solenoid solenoid1, Solenoid solenoid2,
			ADXRS450_Gyro gyro,
			String leftDriveFgainDashboardKey, double defaultLeftDriveFgain,
			String rightDriveFgainDashboardKey, double defaultRightDriveFgain,
			boolean leftMotorDefaultDirection, boolean leftSlaveMotorDefaultDirection,
			boolean rightMotorDefaultDirection, boolean rightSlaveMotorDefaultDirection,
			double robotWidth, double wheelCircumference, double encoderEdgesPerRevolution,
			double autonomousMaxVelocity, double autonomousMaxAcceleration,
			double highGearRatio, double lowGearRatio,
			Joystick joystick, Joystick wheel) {
		super(
				leftMotor, rightMotor,
				leftMotorSlave, rightMotorSlave,
				solenoid1, solenoid2,
				gyro,
				leftDriveFgainDashboardKey, defaultLeftDriveFgain,
				rightDriveFgainDashboardKey, defaultRightDriveFgain,
				leftMotorDefaultDirection, leftSlaveMotorDefaultDirection,
				rightMotorDefaultDirection, rightSlaveMotorDefaultDirection,
				robotWidth, wheelCircumference, encoderEdgesPerRevolution,
				autonomousMaxVelocity, autonomousMaxAcceleration,
				highGearRatio, lowGearRatio);
		this.joystick = joystick;
		this.wheel = wheel;
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DriveWithJoystickCommand(this, joystick, wheel));
	}
	
	public void zeroEncoders() {
		leftMotor.setSelectedSensorPosition(0, 0, 10);
		rightMotor.setSelectedSensorPosition(0, 0, 10);
	}
}
