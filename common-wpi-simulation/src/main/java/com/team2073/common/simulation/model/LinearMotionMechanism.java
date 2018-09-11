package com.team2073.common.simulation.model;

import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class LinearMotionMechanism extends AbstractSimulationMechanism implements SimulationMechanism {

	private final double gearRatio;
	private double massOnSystem;
	private double velocityConstant;
	private double torqueConstant;
	private double pulleyRadius;
	private double motorResistance;
	private double currentVoltage = 0;

	/**
	 * For Systems like elevators =)
	 * <p>
	 * Units are in terms of RPM, Inches, and Pounds
	 *
	 * @param gearRatio    Should be > 1, from motor to output
	 * @param motor        The Type of motor is the system running on.
	 * @param motorCount   The number of motors for the system.
	 * @param massOnSystem How much weight are we pulling up. (Probably want to overestimate this kV bit)
	 */
	public LinearMotionMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double pulleyRadius) {
		this.gearRatio = gearRatio;
		this.massOnSystem = massOnSystem;
		this.pulleyRadius = pulleyRadius;

		velocityConstant = motor.velocityConstant;
		torqueConstant = motor.torqueConstant;
		motorResistance = motor.motorResistance;

//		doubles the stall torque to make "super motor" based on motor count
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
		acceleration = (-torqueConstant * gearRatio * gearRatio
				/ (velocityConstant * motorResistance * pulleyRadius * pulleyRadius * massOnSystem)
				* velocity
				+ gearRatio * torqueConstant / (motorResistance * pulleyRadius * massOnSystem) * currentVoltage);

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
