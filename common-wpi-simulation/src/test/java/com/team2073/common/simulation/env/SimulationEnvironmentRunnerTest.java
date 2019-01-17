package com.team2073.common.simulation.env;

import com.team2073.common.simulation.env.SubsystemTestFixtures.BasicCycleComponent;
import com.team2073.common.simulation.env.SubsystemTestFixtures.BasicPeriodicComponent;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pbriggs
 */
class SimulationEnvironmentRunnerTest extends BaseWpiTest {

	@Test
	void testBasicRun() {
		BasicCycleComponent cycle;
		BasicPeriodicComponent periodic;
		SimulationEnvironment env = SimulationEnvironmentRunner.create()
				.withCycleComponent(cycle = new BasicCycleComponent())
				.withPeriodicComponent(periodic = new BasicPeriodicComponent())
				.start();
		
		assertEquals(cycle.getCycles(), env.getCurrCycle(),
				"Expected cycle iterations did not match actual.");
		assertEquals(periodic.getCycles(), env.getCurrRobotPeriodic(),
				"Expected periodic iterations did not match actual.");
	}


}