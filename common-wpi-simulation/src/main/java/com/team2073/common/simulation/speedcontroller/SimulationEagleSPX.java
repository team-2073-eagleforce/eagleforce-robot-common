package com.team2073.common.simulation.speedcontroller;

import com.team2073.common.simulation.model.SimulationMechanism;

public class SimulationEagleSPX extends BaseSimulationMotorController {

    /**
     * Simulated TalonSPX
     *
     * @param name      This is the name the Talon will be referred to in logging.
     * @param mechanism The mechanism that is being controlled by this talon
     */
    public SimulationEagleSPX(String name, SimulationMechanism mechanism) {
        super(name, mechanism);
    }
}
