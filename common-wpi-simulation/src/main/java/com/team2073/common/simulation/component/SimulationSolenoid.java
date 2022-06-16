package com.team2073.common.simulation.component;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class SimulationSolenoid extends Solenoid {
	private boolean output;

	public SimulationSolenoid(PneumaticsModuleType moduleType, int channel) {
		super(moduleType, channel);
	}

	@Override
	public void set(boolean on) {
		output = on;
	}

	@Override
	public boolean get() {
		return output;
	}


}
