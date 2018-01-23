package com.team2073.common.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.util.TalonHelper;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class AbstractEagledriveSubsystem extends Subsystem {
	protected final TalonSRX leftMotor;
	protected final TalonSRX rightMotor;
	protected final TalonSRX leftMotorSlave;
	protected final TalonSRX rightMotorSlave;
	
	public AbstractEagledriveSubsystem(
			TalonSRX leftMotor, TalonSRX rightMotor,
			TalonSRX leftMotorSlave, TalonSRX rightMotorSlave) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftMotorSlave = leftMotorSlave;
		this.rightMotorSlave = rightMotorSlave;
		setSlaves();
	}
	
	private void setSlaves() {
		TalonHelper.setFollowerOf(leftMotorSlave, leftMotor);
		TalonHelper.setFollowerOf(rightMotorSlave, rightMotor);
	}
	
	protected abstract double getSense();
	
	public double turnSense(double ptart) {
		double sense = getSense();
		return sense * ptart * ptart * ptart + ptart * (1 - sense);
	}
	
	protected abstract double getInverse();

	public double inverse(double start) {
		double inverse = getInverse();
		return (start) * inverse + start;
	}

	public void pointTurn(double turn) {
		rightMotor.set(ControlMode.PercentOutput, -turn);
		leftMotor.set(ControlMode.PercentOutput, -turn);
	}
	
	protected abstract boolean isBallIntakeForwards();

	public void move(double speed, double turn) {
		double rightSide = -(inverse(speed) - (inverse(speed) * turnSense(turn)));
		double leftSide = inverse(speed) + (inverse(speed) * turnSense(turn));

		if (isBallIntakeForwards()) {
			rightMotor.set(ControlMode.PercentOutput, rightSide);
			leftMotor.set(ControlMode.PercentOutput, leftSide);
		} else {
			leftMotor.set(ControlMode.PercentOutput, rightSide);
			rightMotor.set(ControlMode.PercentOutput, leftSide);
		}
	}
}
