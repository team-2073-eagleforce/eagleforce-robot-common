package com.team2073.common.simulation.model;

import com.team2073.common.simulation.env.SimulationEnvironment;

/**
 * @author pbriggs
 */
public interface SimulationCycleComponent {

    void cycle(SimulationEnvironment env);

}
