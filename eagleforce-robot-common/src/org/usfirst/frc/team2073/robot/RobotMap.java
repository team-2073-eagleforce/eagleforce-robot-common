/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2073.robot;

import org.usfirst.frc.team2073.conf.AppConstants.RobotPorts;
import org.usfirst.frc.team2073.conf.AppConstants.Subsystems.BallIntake;
import org.usfirst.frc.team2073.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.subsystems.IntakeSubsystem;
import org.usfirst.frc.team2073.subsystems.DrivetrainSubsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
	private static TalonSRX intakeMotor = new TalonSRX(RobotPorts.INTAKEMOTOR);
	private static boolean intakeForwards = false;
	
	private static IntakeSubsystem intake;
	

	public static TalonSRX getIntakeMotor() {
		return intakeMotor;
	}

	public static boolean isIntakeForwards() {
		return intakeForwards;
	}

	public static IntakeSubsystem getIntake() {
		return intake;
	}

	static void init() {
		drivetrain = new DrivetrainSubsystem();
		intake = new IntakeSubsystem();

		SmartDashboard.putData(Drivetrain.NAME, drivetrain);
		SmartDashboard.putData(BallIntake.NAME, intake);
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
		return intakeForwards;
	}
	
	
}
