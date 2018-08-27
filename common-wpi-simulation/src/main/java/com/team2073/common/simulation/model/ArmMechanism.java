package com.team2073.common.simulation.model;


import com.team2073.common.simulation.SimulationConstants.Motors;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.msToSeconds;

public class ArmMechanism implements SimulationMechanism {
	
	private final double gearRatio;
	private final MotorType motor;
	private final int motorCount;
	private double massOnSystem;
	private double velocityConstant; 
	private double torqueConstant;
	private double lengthOfArm;
	private double motorResistance;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;
	
	public enum MotorType {
		PRO, BAG, CIM, MINI_CIM;
	}
	
	/**
	 * For Arm Systems with most of the weight at the end of the arm
	 *
	 * Units are in terms of RPM, Inches, and Pounds
	 * 
	 * @param gearRatio
	 * Should be > 1, from motor to output 
	 * 
	 * @param motor
	 * The Type of motor is the system running on.
	 * @param motorCount
	 * The number of motors for the system.
	 * @param massOnSystem
	 * How much weight are we pulling up. (Probably want to overestimate this a bit)
	 */
	public ArmMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double lengthOfArm) {
		this.gearRatio = gearRatio;
		this.motor = motor;
		this.motorCount = motorCount;
		this.massOnSystem = massOnSystem;
		this.lengthOfArm = lengthOfArm;
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
		
//		Creates "supermotor" with additional torque per motor
		torqueConstant = torqueConstant * 2 * motorCount;
	}

	@Override
	public void updateVoltage(double voltage) {
		currentVoltage  = voltage;
	}
	
	@Override
	public void cycle(SimulationEnvironment env) {
		calculateDistance(env.getIntervalMs());
		calculateMechanismVelocity(env.getIntervalMs(), currentVoltage);
	}
	
	private double calculateMechanismAcceleration(double voltage) {
		currentMechanismAcceleration = ((gearRatio * torqueConstant * voltage)
				- ((1/velocityConstant) * torqueConstant * currentMechanismVelocity * gearRatio * gearRatio))
				/ (motorResistance * (1./12.) * massOnSystem * Math.pow(lengthOfArm, 2));
				
		return currentMechanismAcceleration;
	}

//	private void calculateNewMotorVelocity(double voltage, int intervalInMs) {
//		// TODO: Check units
//		// InInchesPerMinute^2 at the mechanism
//		currentMechanismVelocity += currentMechanismAcceleration * msToMinutes(intervalInMs);
//		double motorVelocity = (currentMechanismVelocity * gearRatio) / (pullyRadius * Math.PI * 2);
//		currentMotorSpeed = motorVelocity;
//	}
	
	private void calculateMechanismVelocity(int intervalInMs, double voltage) {
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration(voltage);
	}
	
	private void calculateDistance(int intervalInMs) {
//		currentMechanismPosition += currentMechanismVelocity * msToSeconds(intervalInMs)
//				+ (1 / 2) * currentMechanismAcceleration * Math.pow(msToSeconds(intervalInMs), 2);
		currentMechanismPosition += msToSeconds(intervalInMs)*currentMechanismVelocity;
	}

	public double getCurrentMechanismPosition() {
		return currentMechanismPosition;
	}

	public double getCurrentMechanismVelocity() {
		return currentMechanismVelocity;
	}

	public double getCurrentMechanismAcceleration() {
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
