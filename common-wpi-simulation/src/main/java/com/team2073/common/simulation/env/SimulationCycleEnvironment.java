package com.team2073.common.simulation.env;

import com.team2073.common.assertion.Assert;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pbriggs
 */
public class SimulationCycleEnvironment implements SimulationCycleComponent {

    private Logger log = LoggerFactory.getLogger(getClass());

    private List<SimulationCycleComponent> componentList = new ArrayList<>();

    public void registerCycleComponent(SimulationCycleComponent component) {
        Assert.assertNotNull(component, "component");
        componentList.add(component);
    }

    @Override
    public void cycle(SimulationEnvironment env) {
        componentList.forEach(e -> {
            log.trace("SimCycleEnv: Invoking cycle component [{}]...", e.getClass().getSimpleName());
            e.cycle(env);
            log.trace("SimCycleEnv: Invoking cycle component [{}] complete.", e.getClass().getSimpleName());
        });
    }
}
