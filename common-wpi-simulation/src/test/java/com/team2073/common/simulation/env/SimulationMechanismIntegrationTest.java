package com.team2073.common.simulation.env;

import com.team2073.common.CommonConstants.TestTags;
import com.team2073.common.simulation.SimulationConstants;
import com.team2073.common.simulation.component.SimulationComponentFactory;
import com.team2073.common.simulation.component.SimulationSolenoid;
import com.team2073.common.simulation.env.SubsystemTestFixtures.ConstantOutputtingSubsystem;
import com.team2073.common.simulation.env.SubsystemTestFixtures.SolenoidSubsystem;
import com.team2073.common.simulation.model.ArmMechanism;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSPX;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import com.team2073.common.simulation.subsystem.SimulatedElevatorSubsystem;
import com.team2073.common.simulation.subsystem.SimulatedMotionProfileElevatorSubsystem;
import com.team2073.common.wpitest.BaseWpiTest;
import edu.wpi.first.wpilibj.DigitalInput;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag(TestTags.INTEGRATION_TEST)
class SimulationMechanismIntegrationTest extends BaseWpiTest {

	@Test
	void simulationEagleSRX_WHEN_set_SHOULD_MoveMechanism() {
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.CIM, 2, 30, .855);

		SimulationEagleSRX talon = new SimulationEagleSRX("ExampleTalon", lmm, 4096);
        robotContext.getPeriodicRunner().register( talon);
		ConstantOutputtingSubsystem subsystem = new ConstantOutputtingSubsystem(talon);

		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
				.withPeriodicRunner(robotContext.getPeriodicRunner())
				.withIterationCount(50)
				.start(e -> {
					assertTrue(lmm.velocity() > 0);
				});

	}

	@Test
	void simulationEagleSPX_WHEN_set_SHOULD_MoveMechanism() {
		ArmMechanism arm = new ArmMechanism(55, SimulationConstants.MotorType.MINI_CIM, 2, 15, 13);
		SimulationEagleSPX victor = new SimulationEagleSPX("ExampleTalon", arm);
        robotContext.getPeriodicRunner().register(victor);
		ConstantOutputtingSubsystem subsystem = new ConstantOutputtingSubsystem(victor);
		new SimulationEnvironmentRunner()
				.withCycleComponent(arm)
				.withPeriodicComponent(subsystem)
				.withPeriodicRunner(robotContext.getPeriodicRunner())
				.withIterationCount(50)
				.start(e -> assertTrue(arm.velocity() > 0));

	}

	@Test
	void subsystemSimulation_WHEN_usedWithControlloop_SHOULD_moveAccordingly() {
		double goalPosition = 25;

//		Creates mechanism that will be simulated. Specify gear Ratio, motor types, and other physical properties of the mechanism.
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.PRO, 2, 20, .855);

//		Create each component for the subsystem, these will have additional parameters than their non Simulation counterparts
//      due to information like where the physical sensor is, knowledge of the mechanism they are interacting with, etc.
		SimulationEagleSRX srx = new SimulationEagleSRX("ExampleTalon", lmm, 1350);
        robotContext.getPeriodicRunner().register(srx);
//		Think hall effect sensor or limit switch, pass in the values at which mechanism position will it be reading true and the width that it reads them.
		DigitalInput sensor = SimulationComponentFactory.createSimulationDigitalInput(lmm, goalPosition, .5);

//		This one is pretty simple, but make sure you know what the values of the piston mean on the actual robot.
		SimulationSolenoid solenoid = SimulationComponentFactory.createSimulationSolenoid(lmm);

//		Create the subsystem, just like normal, but pass in your simulation components. Make sure the subsystem never instantiates objects from wpilib.
//		No changes to the subsystem should be made between working on the robot and running simulation, if things change, your testing could be invalidated.
		SimulatedElevatorSubsystem subsystem = new SimulatedElevatorSubsystem(srx, sensor, solenoid);

//		Tell the mechanism what to do when the solenoid is active, this will often differ for different mechanisms, and many won't even have a solenoid.
		lmm.whenSolenoidActive(() -> {
			lmm.setVelocity(0);
			lmm.setAcceleration(0);
		});

//		This is just giving hte subsystem a setpoint, nothing fancy here.
		subsystem.set(goalPosition);

//		This is the big fancy SimulationEnvironment Runner, it will handle running the "Real World" cycle, and the software periodic loops,
//      just pass in your mechanism, subsystem, and tell how long you want it to run for before executing the methods in the run method.
//      (That is where you should place your assertions.)
		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
                .withPeriodicRunner(robotContext.getPeriodicRunner())
				.withIterationCount(300)
				.start(e -> {
					assertThat(lmm.position()).isCloseTo(goalPosition, offset(2.0));
				});

	}

	@Test
	void subsystemSimulation_WHEN_usedWithMotionProfile_SHOULD_moveAccordingly() {

		double goalPosition = 25;

		LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.PRO, 2, 20, .855);

		SimulationEagleSRX srx = new SimulationEagleSRX("ExampleTalon", lmm, 1350);
        robotContext.getPeriodicRunner().register(srx);
		DigitalInput sensor = SimulationComponentFactory.createSimulationDigitalInput(lmm, goalPosition, .5);

		SimulationSolenoid solenoid = SimulationComponentFactory.createSimulationSolenoid(lmm);

		SimulatedMotionProfileElevatorSubsystem subsystem = new SimulatedMotionProfileElevatorSubsystem(srx, sensor, solenoid);

		lmm.whenSolenoidActive(() -> {
			lmm.setVelocity(0);
			lmm.setAcceleration(0);
		});

		subsystem.set(goalPosition);

		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
                .withPeriodicRunner(robotContext.getPeriodicRunner())
				.withIterationCount(300)
				.start(e -> {
					assertThat(lmm.position()).isCloseTo(goalPosition, offset(5.0));
				});

	}

	@Test
	void simulationSolenoid_WHEN_set_SHOULD_MoveMechanism() {
		ArmMechanism arm = new ArmMechanism(55, SimulationConstants.MotorType.MINI_CIM, 2, 15, 13);
		SolenoidSubsystem subsystem = new SolenoidSubsystem(SimulationComponentFactory.createSimulationSolenoid(arm));

		new SimulationEnvironmentRunner()
				.withCycleComponent(arm)
				.withPeriodicComponent(subsystem)
				.withIterationCount(5)
				.start(e -> assertTrue(arm.isSolenoidExtended()));
	}


}
