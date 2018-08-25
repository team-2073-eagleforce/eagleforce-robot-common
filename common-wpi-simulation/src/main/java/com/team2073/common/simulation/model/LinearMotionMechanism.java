package com.team2073.common.simulation.model;


import com.team2073.common.simulation.SimulationConstants.Motors;
import com.team2073.common.simulation.env.SimulationEnvironment;

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
	
	public enum MotorType {
		PRO, BAG, CIM, MINI_CIM;
	}
	
	public static void main(String args[]) {
		LinearMotionMechanism LMM = new LinearMotionMechanism(25., MotorType.CIM, 2, 30, .855);
		for(int i = 0; LMM.getCurrentMechanismPosition() < 40; i++) {
			LMM.updateVoltage(12);
//			LMM.cycle(1);
			if(i%100 == 0)
				System.out.println(i+":" + "\tPosition: " + LMM.getCurrentMechanismPosition() + " \t Velocity: " + LMM.getCurrentMechanismVelocity() + "\t Acceleration: " + LMM.getCurrentMechanismAcceleration());
		}
	}
	
	/**
	 * For Systems like elevators =)
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
		currentVoltage  = voltage;
	}
	
	@Override
	public void cycle(SimulationEnvironment env) {
		calculateDistance(env.getIntervalMs());
		calculateMechanismVelocity(env.getIntervalMs());
//		calculateMechanismAcceleration(currentVoltage);
//		calculateNewMotorVelocity(currentVoltage, intervalInMs);
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

//	private void calculateNewMotorVelocity(double voltage, int intervalInMs) {
//		// TODO: Check units
//		// InInchesPerMinute^2 at the mechanism
//		currentMechanismVelocity += currentMechanismAcceleration * msToMinutes(intervalInMs);
//		double motorVelocity = (currentMechanismVelocity * gearRatio) / (pullyRadius * Math.PI * 2);
//		currentMotorSpeed = motorVelocity;
//	}
	
	private void calculateMechanismVelocity(int intervalInMs) {
//		currentMechanismVelocity =  currentMechanismVelocity + currentMechanismAcceleration * msToSeconds(intervalInMs);
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration();
	}
	
	private double msToSeconds(int timeInMs) {
		return timeInMs * 0.001;
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
