package org.usfirst.frc.team2073.interfaces;

import org.usfirst.frc.team2073.RobotMap;
import org.usfirst.frc.team2073.conf.AppConstants;
import org.usfirst.frc.team2073.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.util.TalonHelper;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AbstractEagledriveSubsystem extends Subsystem {
	TalonSRX leftMotor = RobotMap.getLeftMotor();
	TalonSRX rightMotor = RobotMap.getRightMotor();
	TalonSRX leftMotorSlave = RobotMap.getLeftMotorSlave();
	TalonSRX rightMotorSlave = RobotMap.getRightMotorSlave();
	
	public AbstractEagledriveSubsystem() {
		setSlaves();
	}
	
	private void setSlaves() {
		TalonHelper.setFollowerOf(leftMotorSlave, leftMotor);
		TalonHelper.setFollowerOf(rightMotorSlave, rightMotor);
	}
	
	public double turnSense(double ptart) {
		double sense = SmartDashboard.getNumber(DashboardKeys.SENSE, AppConstants.Defaults.DEFAULT_SENSE);
		return sense * ptart * ptart * ptart + ptart * (1 - sense);
	}

	public double inverse(double start) {
		double inverse = SmartDashboard.getNumber(DashboardKeys.INVERSE, AppConstants.Defaults.DEFAULT_INVERSE);
		return (start) * inverse + start;
	}

	public void pointTurn(double turn) {
		rightMotor.set(ControlMode.PercentOutput, -turn);
		leftMotor.set(ControlMode.PercentOutput, -turn);
	}

	public void move(double speed, double turn) {

		double rightSide = -(inverse(speed) - (inverse(speed) * turnSense(turn)));
		double leftSide = inverse(speed) + (inverse(speed) * turnSense(turn));

		if (RobotMap.isBallIntakeForwards()) {
			rightMotor.set(ControlMode.PercentOutput, rightSide);
			leftMotor.set(ControlMode.PercentOutput, leftSide);
		} else {
			leftMotor.set(ControlMode.PercentOutput, rightSide);
			rightMotor.set(ControlMode.PercentOutput, leftSide);
		}
	}
}
