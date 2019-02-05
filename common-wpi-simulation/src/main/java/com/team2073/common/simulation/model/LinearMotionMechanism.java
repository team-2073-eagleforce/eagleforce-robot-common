package com.team2073.common.simulation.model;

import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

import static com.team2073.common.util.ConversionUtil.*;

/**
 * For Systems like elevators =)
 *
 * @author Jason Stanley
 */
public class LinearMotionMechanism extends AbstractSimulationMechanism {

	private final double pulleyRadius;

	public LinearMotionMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double pullyRadius) {
		super(gearRatio, motor, motorCount, massOnSystem);
		this.pulleyRadius = inchesToMeters(pullyRadius);
	}

	@Override
	public void updateVoltage(double voltage) {
		currentVoltage = voltage;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		super.cycle(env);

		setPosition(calculatePosition(env.getIntervalMs()));
		setVelocity(calculateVelocity(env.getIntervalMs()));
	}

	/**
	 * Calculates the Mechanism's acceleration given the current mechanism velocity and voltage operating on the motors.
	 * a = (Volt * K_t * G * r)/ (m * R) - (V * K_t * G^2 * r) / ( K_v * m * R)
	 */
	@Override
	public double calculateAcceleration() {
		acceleration = ((currentVoltage * torqueConstant * gearRatio * pulleyRadius) / (massOnSystem * motorResistance))
				- (((inchesToMeters(velocity) * Math.pow(gearRatio, 2)) * pulleyRadius) / (velocityConstant * lbToKg(massOnSystem) * motorResistance));
		return metersToInches(acceleration);
	}

	/**
	 * Integrates over the Acceleration to find how much our velocity has changed in the past interval.
	 * v = a * t + v_0
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
	 * p = v * t + p_0
	 *
	 * @param intervalInMs
	 */
	@Override
	public double calculatePosition(int intervalInMs) {
		position += msToSeconds(intervalInMs) * velocity;
		return position;
	}

}
