package com.team2073.common.simulation.env;

import com.team2073.common.CommonConstants.TestTags;
import com.team2073.common.simulation.SimulationConstants;
import com.team2073.common.simulation.component.SimulationComponentFactory;
import com.team2073.common.simulation.env.SubsystemTestFixtures.ConstantOutputtingSubsystem;
import com.team2073.common.simulation.env.SubsystemTestFixtures.SimulatedElevatorSubsystem;
import com.team2073.common.simulation.env.SubsystemTestFixtures.SolenoidSubsystem;
import com.team2073.common.simulation.model.ArmMechanism;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSPX;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import org.assertj.core.api.Assertions;
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

	@Test
	public void subsystemSimulation_WHEN_usedWithControlloop_SHOULD_moveAccordingly() {
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.PRO, 2, 20, .855);
		SubsystemTestFixtures.SimulatedElevatorSubsystem subsystem = new SimulatedElevatorSubsystem(new SimulationEagleSRX("ExampleTalon", lmm, 1350));
		double goalPosition = 25;

		subsystem.set(goalPosition);
		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(100)
				.run(e -> {
					System.out.printf("\n \n POSITION : [%s] \n \n", lmm.position());
					Assertions.assertThat(lmm.position()).isCloseTo(goalPosition, Assertions.offset(2.0));
				});
	}

	@Test
	public void simulationSolenoid_WHEN_set_SHOULD_MoveMechanism() {
		ArmMechanism arm = new ArmMechanism(55, SimulationConstants.MotorType.MINI_CIM, 2, 15, 13);
		SolenoidSubsystem subsystem = new SolenoidSubsystem(SimulationComponentFactory.createSimulationSolenoid(arm));

		new SimulationEnvironmentRunner()
				.withCycleComponent(arm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(5)
				.run(e -> assertTrue(arm.solenoidPosition()));
	}


}
