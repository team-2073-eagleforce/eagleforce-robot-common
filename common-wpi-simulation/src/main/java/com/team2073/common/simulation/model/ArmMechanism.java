package com.team2073.common.simulation.model;


import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class ArmMechanism extends AbstractSimulationMechanism implements SimulationMechanism {

	private final double gearRatio;
	private double massOnSystem;
	private double velocityConstant;
	private double torqueConstant;
	private double lengthOfArm;
	private double motorResistance;
	private double currentVoltage = 0;

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
		super.cycle(env);

		calculatePosition(env.getIntervalMs());
		calculateVelocity(env.getIntervalMs());
	}

	/**
	 * Calculates the Mechanism's acceleration given the current mechanism velocity and voltage operating on the motors.
	 */
	@Override
	public double calculateAcceleration() {
		acceleration = ((gearRatio * torqueConstant * currentVoltage)
				- ((1 / velocityConstant) * torqueConstant * velocity * gearRatio * gearRatio))
				/ (motorResistance * (1. / 12.) * massOnSystem * Math.pow(lengthOfArm, 2));

		return acceleration;
	}

	/**
	 * Integrates over the Acceleration to find how much our velocity has changed in the past interval.
	 *
	 * @param intervalInMs
	 */
	@Override
	public double calculateVelocity(int intervalInMs) {
		velocity += msToSeconds(intervalInMs) * calculateAcceleration();
		return velocity;
	}

	/**
	 * Integrates over the Velocity to find how much our position has changed in the past interval.
	 *
	 * @param intervalInMs
	 */
	@Override
	public double calculatePosition(int intervalInMs) {
		position += msToSeconds(intervalInMs) * velocity;
		return position;
	}

}
