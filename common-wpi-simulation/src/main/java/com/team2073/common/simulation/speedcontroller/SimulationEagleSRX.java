package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.simulation.model.SimulationMechanism;

public class SimulationEagleSRX extends BaseSimulationMotorController implements SimulationMotorControllerEnhanced {

	@DataPointIgnore
	private final double encoderTicsPerUnitOfMechanism;

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

	@Override
	public void selectProfileSlot(int slotIdx, int pidIdx) {
		// I needed this for testing PositionalMechanismController: motor.selectProfileSlot(slotIdx, pidIdx)
	}

	@Override
	public SensorCollection getSensorCollection() {
		throwUnsupported("SensorCollection");
		return null;
	}
}