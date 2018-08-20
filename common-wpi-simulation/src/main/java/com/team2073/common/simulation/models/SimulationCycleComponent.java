package com.team2073.common.simulation.models;

import com.team2073.common.simulation.env.SimulationEnvironment;

/**
 * @author pbriggs
 */
public interface SimulationCycleComponent {

    void cycle(SimulationEnvironment env);

}
