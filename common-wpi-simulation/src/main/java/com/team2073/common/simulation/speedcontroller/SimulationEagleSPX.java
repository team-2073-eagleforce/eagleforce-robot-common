package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.EnumUtil;

public class SimulationEagleSPX extends BaseSimulationMotorController {

	/**
	 * Simulated TalonSPX
	 *
	 * @param name      This is the name the Talon will be referred to in logging.
	 * @param mechanism The mechanism that is being controlled by this talon
	 *
	 */
	public SimulationEagleSPX(String name, SimulationMechanism mechanism) {
		super(name, mechanism);
	}

}
