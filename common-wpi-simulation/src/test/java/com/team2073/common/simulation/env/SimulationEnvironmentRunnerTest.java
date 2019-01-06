package com.team2073.common.simulation.env;

import com.team2073.common.simulation.env.SubsystemTestFixtures.BasicCycleComponent;
import com.team2073.common.simulation.env.SubsystemTestFixtures.BasicPeriodicComponent;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
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
				.withCycleComponent((SimulationCycleComponent) (cycle = new BasicCycleComponent())
				)
				.withPeriodicComponent(periodic = new BasicPeriodicComponent())
				.run(e -> {
					assertEquals(cycle.getCycles(), e.getCurrCycle(),
							"Expected cycle iterations did not match actual.");
					assertEquals(periodic.getCycles(), e.getCurrRobotPeriodic(),
							"Expected periodic iterations did not match actual.");
				});
	}


}