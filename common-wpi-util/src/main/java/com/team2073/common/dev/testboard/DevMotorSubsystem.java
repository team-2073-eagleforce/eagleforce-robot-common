package com.team2073.common.dev.testboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A simple subsystem to use for dev/testing.
 *
 * @author Preston Briggs
 */
public class DevMotorSubsystem extends Subsystem {

	private final TalonSRX talonMotorController;
	
	public DevMotorSubsystem(TalonSRX talonMotorController) {
		System.out.println("Constructing DevMotorSubsystem using TalonSRX: " + talonMotorController);
		this.talonMotorController = talonMotorController;
	}
	
	@Override
	protected void initDefaultCommand() {
	}
	
	/**
	 * Move at a constant rate of 30%. Use {@link #move(double)} to control
	 * the speed.
	 */
	public void move() {
		move(.3);
	}
	
	/**
	 * Move at the given speed.
	 * @param speedPercent Percent of motor output (from -1 to 1).
	 */
	public void move(double speedPercent) {
		System.out.println("DevMotorSubsystem moving at " + (speedPercent * 10) + "%");
		talonMotorController.set(ControlMode.PercentOutput, speedPercent);
	}
	
	/**
	 * Stop the motor.
	 */
	public void stop() {
		System.out.println("DevMotorSubsystem stopping");
		talonMotorController.set(ControlMode.PercentOutput, 0);
	}

}
