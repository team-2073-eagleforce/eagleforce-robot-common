package org.usfirst.frc.team2073.robot.simulation.models;

import org.usfirst.frc.team2073.robot.conf.AppConstants.Motors;

public class LinearMotionMechanism implements Mechanism{
	
	private final double gearRatio;
	private final MotorType motor;
	private final int motorCount;
	private final double massOnSystem;
	private double velocityConstant; 
	private double torqueConstant;
	private double pullyRadius;
	private double motorResitance;
	private double currentMotorSpeed = 0;
	private double currentVoltage = 0;
	private double currentMechanismPosition = 0;
	private double currentMechanismVelocity = 0;
	private double currentMechanismAcceleration = 0;
	
	public enum MotorType {
		PRO, BAG, CIM, MINI_CIM;
	}
	
	public static void main(String args[]) {
		LinearMotionMechanism LMM = new LinearMotionMechanism(25, MotorType.PRO, 2, 40, .855);
		for(int i = 0; i < Math.pow(2, 8); i++) {
			LMM.updateVoltage(i*12/(Math.pow(2, 8)));
			LMM.periodic(100);
			System.out.println("Position: " + LMM.getCurrentMechanismPosition());
			System.out.println("Velocity: " + LMM.getCurrentMechanismVelocity());
			System.out.println("Acceleration: " + LMM.getCurrentMechanismAcceleration());
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
	 * @param encoderCountsPerInchOfTravel
	 * How many encoder counts per inch of travel.
	 * @param massOnSystem
	 * How much weight are we pulling up. (Probably want to overestimate this a bit)
	 */
	public LinearMotionMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double pulleyRadius) {
		this.gearRatio = gearRatio;
		this.motor = motor;
		this.motorCount = motorCount;
		this.massOnSystem = massOnSystem;
		this.pullyRadius = pulleyRadius;
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
		
		torqueConstant *= motorCount;
	}
	
	public void updateVoltage(double voltage) {
		currentVoltage  = voltage;
	}
	
	@Override
	public void periodic(int intervalInMs) {
		calculateMechanismAcceleration(currentVoltage, intervalInMs);
		calculateMechanismVelocity(currentVoltage, intervalInMs);
		calculateNewMotorVelocity(currentVoltage, intervalInMs);
		calculateDistance(intervalInMs);
	}
	
	private void calculateMechanismAcceleration(double voltage, double motorSpeed) {
		double term1 = -motorSpeed * (gearRatio / pullyRadius * velocityConstant);
		double term2 = voltage;
		double denominator = gearRatio * torqueConstant;
		double coef = (motorResitance * pullyRadius * massOnSystem);
		double last = (coef * (term1 + term2)) / (denominator);
		currentMechanismAcceleration = last;
	}

	private void calculateNewMotorVelocity(double voltage, int intervalInMs) {
		// TODO: Check units
		// InInchesPerMinute^2 at the mechanism
		currentMechanismVelocity += currentMechanismAcceleration * msToMinutes(intervalInMs);
		double motorVelocity = (currentMechanismVelocity * gearRatio) / (pullyRadius * Math.PI * 2);
		currentMotorSpeed = motorVelocity;
	}
	
	private void calculateMechanismVelocity(int intervalInMs) {
		currentMechanismVelocity =  currentMechanismVelocity + currentMechanismAcceleration * msToMinutes(intervalInMs);
	}
	
	private double msToMinutes(int timeInMs) {
		return timeInMs / 1/*60000*/;
	}
	
	private void calculateDistance(int intervalInMs) {
		currentMechanismPosition = currentMechanismVelocity * msToMinutes(intervalInMs)
				+ (1 / 2) * currentMechanismAcceleration * Math.pow(msToMinutes(intervalInMs), 2);
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
	
	
}
