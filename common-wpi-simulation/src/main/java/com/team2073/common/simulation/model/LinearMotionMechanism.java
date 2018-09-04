package com.team2073.common.simulation.model;

import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class LinearMotionMechanism implements SimulationMechanism {

	private final double gearRatio;
	private double massOnSystem;
	private double velocityConstant;
	private double torqueConstant;
	private double pulleyRadius;
	private double motorResistance;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;
	private boolean solenoidPosition;
	private Runnable whenPistonActive;

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
		if (solenoidPosition) {
			whenPistonActive.run();
		}

		calculatePosition(env.getIntervalMs());
		calculateMechanismVelocity(env.getIntervalMs());
	}

	/**
	 * Calculates the Mechanism's acceleration given the current mechanism velocity and voltage operating on the motors.
	 */
	private double calculateMechanismAcceleration() {
		currentMechanismAcceleration = (-torqueConstant * gearRatio * gearRatio
				/ (velocityConstant * motorResistance * pulleyRadius * pulleyRadius * massOnSystem)
				* currentMechanismVelocity
				+ gearRatio * torqueConstant / (motorResistance * pulleyRadius * massOnSystem) * currentVoltage);

		return currentMechanismAcceleration;
	}

	/**
	 * Integrates over the Acceleration to find how much our velocity has changed in the past interval.
	 *
	 * @param intervalInMs
	 */
	private void calculateMechanismVelocity(int intervalInMs) {
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration();
	}

	/**
	 * Integrates over the Velocity to find how much our position has changed in the past interval.
	 *
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

	@Override
	public void whenSolenoidActive(Runnable function) {
		this.whenPistonActive = function;
	}

	@Override
	public void setPosition(double position) {
		this.currentMechanismPosition = position;
	}

	@Override
	public void setVelocity(double velocity) {
		this.currentMechanismVelocity = velocity;
	}

	@Override
	public void setAcceleration(double acceleration) {
		this.currentMechanismAcceleration = acceleration;
	}
}
