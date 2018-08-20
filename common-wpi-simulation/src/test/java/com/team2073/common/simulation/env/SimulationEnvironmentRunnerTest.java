package com.team2073.common.simulation.env;

import com.team2073.common.periodic.PeriodicAware;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author pbriggs
 */
class SimulationEnvironmentRunnerTest {

    @Test
    void testBasicRun() {
        BasicCycleComponent cycle;
        BasicPeriodicComponent periodic;
        new SimulationEnvironmentRunner()
                .withCycleComponent(cycle = new BasicCycleComponent())
                .withPeriodicComponent(periodic = new BasicPeriodicComponent())
                .run(e -> {
                    assertEquals(cycle.getCycles(), e.getCurrCycle(),
                            "Expected cycle iterations did not match actual.");
                    assertEquals(periodic.getCycles(), e.getCurrRobotPeriodic(),
                            "Expected periodic iterations did not match actual.");
                });
    }

    private static class BasicCycleComponent implements SimulationCycleComponent{

        int cycles = 0;

        @Override
        public void cycle(SimulationEnvironment env) {
            cycles++;
        }

        public int getCycles() {
            return cycles;
        }
    }

    private static class BasicPeriodicComponent implements PeriodicAware {

        int cycles = 0;


        @Override
        public void onPeriodic() {
            cycles++;
        }

        public int getCycles() {
            return cycles;
        }
    }
}