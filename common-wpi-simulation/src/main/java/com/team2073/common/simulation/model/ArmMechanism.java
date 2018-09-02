package com.team2073.common.simulation.model;


import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class ArmMechanism implements SimulationMechanism {

	private final double gearRatio;
	private double massOnSystem;
	private double velocityConstant;
	private double torqueConstant;
	private double lengthOfArm;
	private double motorResistance;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;
	private boolean solenoidPosition;

	/**
	 * For Arm Systems with most of the weight at the end of the arm
	 * <p>
	 * Units are in terms of RPM, Inches, and Pounds
	 *
	 * @param gearRatio    Should be > 1, from motor to output
	 * @param motor        The Type of motor is the system running on.
	 * @param motorCount   The number of motors for the system.
	 * @param massOnSystem How much weight are we pulling up. (Probably want to overestimate this kV bit)
	 */
	public ArmMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double lengthOfArm) {
		this.gearRatio = gearRatio;
		this.massOnSystem = massOnSystem;
		this.lengthOfArm = lengthOfArm;

		velocityConstant = motor.velocityConstant;
		torqueConstant = motor.torqueConstant;
		motorResistance = motor.motorResistance;

//		Creates "supermotor" with additional torque per motor
		torqueConstant = torqueConstant * 2 * motorCount;
	}

	@Override
	public void updateVoltage(double voltage) {
		currentVoltage = voltage;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		calculatePosition(env.getIntervalMs());
		calculateMechanismVelocity(env.getIntervalMs(), currentVoltage);
	}

	/**
	 * Calculates the Mechanism's acceleration given the current mechanism velocity and voltage operating on the motors.
	 */
	private double calculateMechanismAcceleration(double voltage) {
		currentMechanismAcceleration = ((gearRatio * torqueConstant * voltage)
				- ((1 / velocityConstant) * torqueConstant * currentMechanismVelocity * gearRatio * gearRatio))
				/ (motorResistance * (1. / 12.) * massOnSystem * Math.pow(lengthOfArm, 2));

		return currentMechanismAcceleration;
	}

	/**
	 * Integrates over the Acceleration to find how much our velocity has changed in the past interval.
	 * @param intervalInMs
	 */
	private void calculateMechanismVelocity(int intervalInMs, double voltage) {
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration(voltage);
	}

	/**
	 * Integrates over the Velocity to find how much our position has changed in the past interval.
	 * @param intervalInMs
	 */
	private void calculatePosition(int intervalInMs) {
		currentMechanismPosition += msToSeconds(intervalInMs) * currentMechanismVelocity;
	}

	@Override
	public void updateSolenoid(boolean on) {
		solenoidPosition = on;
	}

	@Override
	public boolean solenoidPosition() {
		return solenoidPosition;
	}

	@Override
	public double acceleration() {
		return currentMechanismAcceleration;
	}

	@Override
	public double position() {
		return currentMechanismPosition;
	}

	@Override
	public double velocity() {
		return currentMechanismVelocity;
	}


}
