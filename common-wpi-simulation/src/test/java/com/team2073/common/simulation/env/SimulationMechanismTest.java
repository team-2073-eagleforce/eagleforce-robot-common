package com.team2073.common.simulation.env;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import com.team2073.common.simulation.subsystem.ExamplePositionalSubsystem;
import edu.wpi.first.wpilibj.Talon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import org.mockito.Answers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class SimulationMechanismTest {

	@BeforeEach
    public void setup(){
		ExamplePositionalSubsystem subsystemMock = mock(ExamplePositionalSubsystem.class);

		doAnswer(e -> {
			e.callRealMethod();
			return null;
		}).when(subsystemMock).onPeriodic();

    }

	@Test
	public void linearMechanism(){
		LinearMotionMechanism lmm = new LinearMotionMechanism(25., LinearMotionMechanism.MotorType.CIM, 2, 30, .855);
		ExamplePositionalSubsystem subsystem = new ExamplePositionalSubsystem(new SimulationEagleSRX("ExampleTalon", lmm, 4096));

		new SimulationEnvironmentRunner()
				.withCycleComponent(lmm)
				.withPeriodicComponent(subsystem)
				.run(e -> {
					System.out.printf("CURRENT VELOCITY: [%s] \n", lmm.getCurrentMechanismVelocity());
					System.out.printf("CURRENT POSITION: [%s] \n", lmm.getCurrentMechanismPosition());
					System.out.printf("CURRENT ACCELERATION: [%s] \n", lmm.getCurrentMechanismAcceleration());
					assertTrue(lmm.getCurrentMechanismVelocity()>1);

				});

	}
}
