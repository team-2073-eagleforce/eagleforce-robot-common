package com.team2073.common.simulation.util.function;

import com.team2073.common.simulation.env.SimulationEnvironment;

/**
 * @author pbriggs
 */
@FunctionalInterface
public interface OnSimulationCompleteHandler {

    void onComplete(SimulationEnvironment simulationEnvironmentRunner);

}