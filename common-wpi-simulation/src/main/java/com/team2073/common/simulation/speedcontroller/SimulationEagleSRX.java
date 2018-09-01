package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.EnumUtil;

public class SimulationEagleSRX extends BaseSimulationMotorControllerEnhanced {
	private double outputVoltage;
	private double maxOutputForward = 12;
	private double maxOutputReverse = -12;
	private double encoderTicsPerUnitOfMechanism;
	private SimulationMechanism mechanism;
	private String name;

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
		this.mechanism = mechanism;
		this.name = name;
		this.encoderTicsPerUnitOfMechanism = encoderTicsPerUnitOfMechanism;
	}

	public double talonOutputVoltage() {
		if (outputVoltage >= 0)
			return Math.min(outputVoltage, maxOutputForward);
		else
			return Math.max(outputVoltage, maxOutputReverse);
	}

	@Override
	public void set(ControlMode mode, double outputValue) {
		switch (mode) {
			case Position:
				throw new NotYetImplementedException("Haven't set up native talon pid to work with simulation, use our PIDF controller instead.");
			case PercentOutput:
				outputVoltage = 12 * outputValue;
				break;
			default:
				EnumUtil.throwUnknownValueException(mode);
		}
		mechanism.updateVoltage(talonOutputVoltage());
	}

	@Override
	public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
		this.maxOutputForward = percentOut * 12;
		return null;
	}

	@Override
	public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
		this.maxOutputReverse = percentOut * 12;
		return null;
	}

	public String getName() {
		return name;
	}

	@Override
	public double getMotorOutputPercent() {
		return outputVoltage / 12;
	}

	@Override
	public double getMotorOutputVoltage() {
		return outputVoltage;
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