package com.team2073.common.simulation.model;


import com.team2073.common.simulation.SimulationConstants.Motors;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class LinearMotionMechanism implements SimulationMechanism {

	private final double gearRatio;
	private final MotorType motor;
	private final int motorCount;
	private double massOnSystem;
	private double velocityConstant;
	private double torqueConstant;
	private double pulleyRadius;
	private double motorResistance;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;

	/**
	 * For Systems like elevators =)
	 * <p>
	 * Units are in terms of RPM, Inches, and Pounds
	 *
	 * @param gearRatio    Should be > 1, from motor to output
	 * @param motor        The Type of motor is the system running on.
	 * @param motorCount   The number of motors for the system.
	 * @param massOnSystem How much weight are we pulling up. (Probably want to overestimate this a bit)
	 */
	public LinearMotionMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double pulleyRadius) {
		this.gearRatio = gearRatio;
		this.motor = motor;
		this.motorCount = motorCount;
		this.massOnSystem = massOnSystem;
		this.pulleyRadius = pulleyRadius;
		calculateConstants();
	}

	private void calculateConstants() {
		switch (motor) {
			case PRO:
				velocityConstant = Motors.Pro.MOTOR_KV;
				torqueConstant = Motors.Pro.MOTOR_KT;
				motorResistance = Motors.Pro.RESISTANCE;
				break;
			case BAG:
				velocityConstant = Motors.Bag.MOTOR_KV;
				torqueConstant = Motors.Bag.MOTOR_KT;
				motorResistance = Motors.Bag.RESISTANCE;
				break;
			case CIM:
				velocityConstant = Motors.Cim.MOTOR_KV;
				torqueConstant = Motors.Cim.MOTOR_KT;
				motorResistance = Motors.Cim.RESISTANCE;
				break;
			case MINI_CIM:
				velocityConstant = Motors.MiniCim.MOTOR_KV;
				torqueConstant = Motors.MiniCim.MOTOR_KT;
				motorResistance = Motors.MiniCim.RESISTANCE;
				break;
		}

//		doubles the stall torque to make "super motor" based on motor count
		torqueConstant = torqueConstant * 2 * motorCount;
	}

	@Override
	public void updateVoltage(double voltage) {
		currentVoltage = voltage;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		calculateDistance(env.getIntervalMs());
		calculateMechanismVelocity(env.getIntervalMs());
	}

	private double calculateMechanismAcceleration() {
		currentMechanismAcceleration = (-torqueConstant * gearRatio * gearRatio
				/ (velocityConstant * motorResistance * pulleyRadius * pulleyRadius * massOnSystem)
				* currentMechanismVelocity
				+ gearRatio * torqueConstant / (motorResistance * pulleyRadius * massOnSystem) * currentVoltage);

		return currentMechanismAcceleration;

//				-Kt * kG * kG / (Kv * kResistance * kr * kr * kMass) * velocity_ +
//		           kG * Kt / (kResistance * kr * kMass) * voltage;

	}

	private void calculateMechanismVelocity(int intervalInMs) {
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration();
	}

	private void calculateDistance(int intervalInMs) {
		currentMechanismPosition += msToSeconds(intervalInMs) * currentMechanismVelocity;
	}

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

	public enum MotorType {
		PRO, BAG, CIM, MINI_CIM;
	}


}
