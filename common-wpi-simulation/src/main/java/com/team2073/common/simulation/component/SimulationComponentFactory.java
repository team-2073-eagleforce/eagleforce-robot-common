package com.team2073.common.simulation.component;

import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.MockUtil;

import static org.mockito.Mockito.*;

public class SimulationComponentFactory {

	public static SimulationSolenoid createSimulationSolenoid(SimulationMechanism mechanism) {

		SimulationSolenoid solenoid = mock(SimulationSolenoid.class);

		MockUtil.callRealVoidMethod(solenoid, () -> mechanism.updateSolenoid(solenoid.get())).set(anyBoolean());

		MockUtil.callRealMethod(solenoid.get());

		return solenoid;
	}

	/**
	 * @param mechanism
	 * @param location    The center position of the accompanying mechanism that the sensor will read true at.
	 * @param sensorWidth How much to look above and below the sensor to read high. Ex. location = 5 and sensorWidth = 2 would read high from 3 to 7.
	 * @return
	 */
	public static SimulationDigitalInput createSimulationDigitalInput(SimulationMechanism mechanism, double location, double sensorWidth) {

		SimulationDigitalInput digitalInput = mock(SimulationDigitalInput.class);


		MockUtil.callRealVoidMethod(digitalInput).setMechanism(any());

		MockUtil.callRealVoidMethod(digitalInput).setLocation(anyDouble());

		MockUtil.callRealVoidMethod(digitalInput).setOffset(anyDouble());

		MockUtil.callRealMethod(digitalInput.get());

		digitalInput.setMechanism(mechanism);
		digitalInput.setLocation(location);
		digitalInput.setOffset(sensorWidth);

		return digitalInput;
	}

}
