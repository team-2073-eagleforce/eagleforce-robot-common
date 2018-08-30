package com.team2073.common.simulation.env;

import com.team2073.common.simulation.env.SubsystemTestFixtures.ConstantOutputtingSubsystem;
import com.team2073.common.simulation.model.ArmMechanism;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSPX;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SimulationMechanismTest {

	@Test
	public void simulationEagleSRXBasic() {
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., LinearMotionMechanism.MotorType.CIM, 2, 30, .855);
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
	public void simulationEagleSPXBasic() {
		ArmMechanism arm = new ArmMechanism(55, ArmMechanism.MotorType.MINI_CIM, 2, 15, 13);
		ConstantOutputtingSubsystem subsystem = new ConstantOutputtingSubsystem(new SimulationEagleSPX("ExampleTalon", arm));

		new SimulationEnvironmentRunner()
				.withCycleComponent(arm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(5)
				.run(e -> assertTrue(arm.velocity() > 0));

	}


}
