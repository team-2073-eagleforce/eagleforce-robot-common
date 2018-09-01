package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.EnumUtil;

public class SimulationEagleSPX extends BaseSimulationMotorController {
	private double outputVoltage;
	private double maxVoltageForward = 12;
	private double maxVoltageReverse = -12;
	private SimulationMechanism mechanism;
	private String name;

	/**
	 * Simulated TalonSPX
	 *
	 * @param name      This is the name the Talon will be referred to in logging.
	 * @param mechanism The mechanism that is being controlled by this talon
	 *
	 */
	public SimulationEagleSPX(String name, SimulationMechanism mechanism) {
		this.mechanism = mechanism;
		this.name = name;
	}

	public double talonOutputVoltage() {
		if (outputVoltage >= 0)
			return Math.min(outputVoltage, maxVoltageForward);
		else
			return Math.max(outputVoltage, maxVoltageReverse);
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
		this.maxVoltageForward = percentOut * 12;
		return null;
	}

	@Override
	public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
		this.maxVoltageReverse = percentOut * 12;
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


}
