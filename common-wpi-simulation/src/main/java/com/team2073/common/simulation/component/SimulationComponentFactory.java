package com.team2073.common.simulation.component;

import com.team2073.common.simulation.model.SimulationMechanism;

import static org.mockito.Mockito.*;

public class SimulationComponentFactory {

	public static SimulationSolenoind createSimulationSolenoid(SimulationMechanism mechanism) {

		SimulationSolenoind solenoid = mock(SimulationSolenoind.class);

		doAnswer(e -> {
			e.callRealMethod();
			mechanism.updateSolenoid(solenoid.get());
			return null;
		}).when(solenoid).set(anyBoolean());

		when(solenoid.get()).thenCallRealMethod();

		return solenoid;
	}


}
