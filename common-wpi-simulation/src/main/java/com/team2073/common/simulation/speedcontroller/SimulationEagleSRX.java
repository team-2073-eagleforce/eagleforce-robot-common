package com.team2073.common.simulation.speedcontroller;

import com.team2073.common.simulation.model.SimulationMechanism;

public class SimulationEagleSRX extends BaseSimulationMotorController implements SimulationMotorControllerEnhanced{
	private double encoderTicsPerUnitOfMechanism;

	/**
	 * Simulated TalonSRX
	 *
	 * @param name This is the name the Talon will be referred to in logging.
	 * @param mechanism The mechanism that is being controlled by this talon
	 * @param encoderTicsPerUnitOfMechanism
	 *  How many encoder tics are expected per unit of the mechanism
	 *  (usually inches or rotations, ie: Elevator = tics/inch, pivotingArm = ticsPerRevolutionOfArm, etc)
	 */
	public SimulationEagleSRX(String name, SimulationMechanism mechanism, int encoderTicsPerUnitOfMechanism) {
		super(name, mechanism);
		this.encoderTicsPerUnitOfMechanism = encoderTicsPerUnitOfMechanism;
	}

	@Override
	public int getSelectedSensorPosition(int pidIdx) {
		return (int) Math.round(mechanism.position() * encoderTicsPerUnitOfMechanism);
	}

	@Override
	public int getSelectedSensorVelocity(int pidIdx) {
		return (int) Math.round(mechanism.velocity() * encoderTicsPerUnitOfMechanism);
	}

}