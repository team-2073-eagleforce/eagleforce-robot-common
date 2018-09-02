package com.team2073.common.simulation.env;

import com.team2073.common.CommonConstants.TestTags;
import com.team2073.common.simulation.SimulationConstants;
import com.team2073.common.simulation.env.SubsystemTestFixtures.ConstantOutputtingSubsystem;
import com.team2073.common.simulation.model.ArmMechanism;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSPX;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestTags.INTEGRATION_TEST)
public class SimulationMechanismIntegrationTest {

	@Test
	public void simulationEagleSRX_WHEN_set_SHOULD_MoveMechanism() {
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.CIM, 2, 30, .855);
		ConstantOutputtingSubsystem subsystem = new ConstantOutputtingSubsystem(new SimulationEagleSRX("ExampleTalon", lmm, 4096));

		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(5)
				.run(e -> {
					assertTrue(lmm.velocity() > 0);
				});

	}

	@Test
	public void simulationEagleSPX_WHEN_set_SHOULD_MoveMechanism() {
		ArmMechanism arm = new ArmMechanism(55, SimulationConstants.MotorType.MINI_CIM, 2, 15, 13);
		ConstantOutputtingSubsystem subsystem = new ConstantOutputtingSubsystem(new SimulationEagleSPX("ExampleTalon", arm));

		new SimulationEnvironmentRunner()
				.withCycleComponent(arm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(5)
				.run(e -> assertTrue(arm.velocity() > 0));

	}


}
