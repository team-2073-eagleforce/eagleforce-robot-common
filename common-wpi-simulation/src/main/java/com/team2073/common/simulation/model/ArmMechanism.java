package com.team2073.common.simulation.model;


import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.*;

/**
 * For Arm Systems with most of the weight at the end of the arm
 *
 * @author Jason Stanley
 */
public class ArmMechanism extends AbstractSimulationMechanism {

	@DataPointIgnore
	private final double lengthOfArm;

	@DataPointIgnore
	private final double moment;

	public ArmMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double lengthOfArm) {
		super(gearRatio, motor, motorCount, massOnSystem);
		this.lengthOfArm = lengthOfArm;
		moment = .5 * inchesToMeters(lengthOfArm) * inchesToMeters(lengthOfArm) * lbToKg(massOnSystem);
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
		acceleration = ((currentVoltage * torqueConstant * gearRatio) / (moment * motorResistance))
				- ((torqueConstant * Math.pow(gearRatio, 2) * degreesToRadians(velocity)) / (velocityConstant * moment * motorResistance));
		return radiansToDegrees(acceleration);
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
