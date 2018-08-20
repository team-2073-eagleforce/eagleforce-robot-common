package com.team2073.common.simulation.env;

import com.team2073.common.assertion.Assert;
import com.team2073.common.simulation.model.SimulationCycleComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pbriggs
 */
public class SimulationCycleEnvironment implements SimulationCycleComponent {

    private List<SimulationCycleComponent> componentList = new ArrayList<>();

    public void registerCycleComponent(SimulationCycleComponent component) {
        Assert.assertNotNull(component, "component");
        componentList.add(component);
    }

    @Override
    public void cycle(SimulationEnvironment env) {
        componentList.forEach(e -> e.cycle(env));
    }
}
