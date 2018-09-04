package com.team2073.common.simulation.component;

import com.team2073.common.simulation.model.SimulationMechanism;
import edu.wpi.first.wpilibj.DigitalInput;

import static org.mockito.Mockito.*;

public class SimulationComponentFactory {

	public static SimulationSolenoid createSimulationSolenoid(SimulationMechanism mechanism) {

		SimulationSolenoid solenoid = mock(SimulationSolenoid.class);

		doAnswer(e -> {
			e.callRealMethod();
			mechanism.updateSolenoid(solenoid.get());
			return null;
		}).when(solenoid).set(anyBoolean());

		when(solenoid.get()).thenCallRealMethod();

		return solenoid;
	}

	/**
	 * @param mechanism
	 * @param location    The center position of the accompanying mechanism that the sensor will read true at.
	 * @param sensorWidth How much to look above and below the sensor to read high. Ex. location = 5 and sensorWidth = 2 would read high from 3 to 7.
	 * @return
	 */
	public static DigitalInput createSimulationDigitalInput(SimulationMechanism mechanism, double location, double sensorWidth) {

		DigitalInput digitalInput = mock(DigitalInput.class);

		when(digitalInput.get()).thenReturn((mechanism.position() > location - sensorWidth) && (mechanism.position() < location + sensorWidth));

		return digitalInput;
	}

}
