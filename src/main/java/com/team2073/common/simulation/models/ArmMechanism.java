package com.team2073.common.simulation.models;

import com.team2073.common.AppConstants.Motors;

public class ArmMechanism implements Mechanism{
	
	private final double gearRatio;
	private final MotorType motor;
	private final int motorCount;
	private double massOnSystem;
	private double velocityConstant; 
	private double torqueConstant;
	private double lengthOfArm;
	private double motorResitance;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;
	
	public enum MotorType {
		PRO, BAG, CIM, MINI_CIM;
	}
	
	public static void main(String args[]) {
		ArmMechanism LMM = new ArmMechanism(55, MotorType.MINI_CIM, 2, 15, 13);
		for(int i = 0; LMM.getCurrentMechanismPosition() < .25; i++) {
			LMM.updateVoltage(12);
			LMM.periodic(1);
			if(i%25 == 0)
				System.out.println(i+":" + "\tPosition: " + LMM.getCurrentMechanismPosition() + " \t Velocity: " + LMM.getCurrentMechanismVelocity() + "\t Acceleration: " + LMM.getCurrentMechanismAcceleration());
		}
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
			motorResitance = Motors.Pro.RESISTANCE;
			break;
		case BAG:
			velocityConstant = Motors.Bag.MOTOR_KV;
			torqueConstant = Motors.Bag.MOTOR_KT;
			motorResitance = Motors.Bag.RESISTANCE;
			break;
		case CIM:
			velocityConstant = Motors.Cim.MOTOR_KV;
			torqueConstant = Motors.Cim.MOTOR_KT;
			motorResitance = Motors.Cim.RESISTANCE;
			break;
		case MINI_CIM:
			velocityConstant = Motors.MiniCim.MOTOR_KV;
			torqueConstant = Motors.MiniCim.MOTOR_KT;
			motorResitance = Motors.MiniCim.RESISTANCE;
			break;
		}
		
//		Creates "supermotor" with additional torque per motor
		torqueConstant = torqueConstant * 2 * motorCount;
	}
	
	public void updateVoltage(double voltage) {
		currentVoltage  = voltage;
	}
	
	@Override
	public void periodic(int intervalInMs) {
		calculateDistance(intervalInMs);
		calculateMechanismVelocity(intervalInMs, currentVoltage);
//		calculateMechanismAcceleration(currentVoltage);
//		calculateNewMotorVelocity(currentVoltage, intervalInMs);
	}
	
	private double calculateMechanismAcceleration(double voltage) {
		currentMechanismAcceleration = ((gearRatio * torqueConstant * voltage)
				- ((1/velocityConstant) * torqueConstant * currentMechanismVelocity * gearRatio * gearRatio))
				/ (motorResitance * (1./12.) * massOnSystem * Math.pow(lengthOfArm, 2));
				
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
//		currentMechanismVelocity =  currentMechanismVelocity + currentMechanismAcceleration * msToSeconds(intervalInMs);
		currentMechanismVelocity += msToSeconds(intervalInMs) * calculateMechanismAcceleration(voltage);
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
	
	
}
