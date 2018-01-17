/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2073.robot;

import org.usfirst.frc.team2073.robot.conf.AppConstants.RobotPorts;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotMap {
	private static DrivetrainSubsystem drivetrain;
	private static TalonSRX leftMotor = new TalonSRX(RobotPorts.LEFT_MOTOR);
	private static TalonSRX leftMotorSlave = new TalonSRX(RobotPorts.LEFT_MOTOR_SLAVE);
	private static TalonSRX rightMotor = new TalonSRX(RobotPorts.RIGHT_MOTOR);
	private static TalonSRX rightMotorSlave = new TalonSRX(RobotPorts.RIGHT_MOTOR_SLAVE);
	private static Solenoid driveSolenoid1 = new Solenoid(RobotPorts.DRIVE_SOLENOID_1);
	private static Solenoid driveSolenoid2 = new Solenoid(RobotPorts.DRIVE_SOLENOID_2);
	private static ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private static boolean ballIntakeForwards = false;
	

	public static ADXRS450_Gyro getGyro() {
		return gyro;
	}


	static void init() {
		drivetrain = new DrivetrainSubsystem();

		SmartDashboard.putData(Drivetrain.NAME, drivetrain);
	}

	public static DrivetrainSubsystem getDrivetrain() {
		return drivetrain;
	}

	public static TalonSRX getLeftMotor() {
		return leftMotor;
	}

	public static TalonSRX getLeftMotorSlave() {
		return leftMotorSlave;
	}

	public static TalonSRX getRightMotor() {
		return rightMotor;
	}

	public static TalonSRX getRightMotorSlave() {
		return rightMotorSlave;
	}

	public static Solenoid getDriveSolenoid1() {
		return driveSolenoid1;
	}

	public static Solenoid getDriveSolenoid2() {
		return driveSolenoid2;
	}

	public static boolean isBallIntakeForwards() {
		return ballIntakeForwards;
	}
	
	
}
