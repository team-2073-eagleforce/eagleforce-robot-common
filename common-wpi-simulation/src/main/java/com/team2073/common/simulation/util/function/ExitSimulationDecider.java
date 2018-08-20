package com.team2073.common.simulation.util.function;

import com.team2073.common.simulation.env.SimulationEnvironment;

/**
 * @author pbriggs
 */
@FunctionalInterface
public interface ExitSimulationDecider {

    boolean shouldExitSimulation(SimulationEnvironment simEnv);

}