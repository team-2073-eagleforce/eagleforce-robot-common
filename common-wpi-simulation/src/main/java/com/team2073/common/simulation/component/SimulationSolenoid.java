package com.team2073.common.simulation.component;

import edu.wpi.first.wpilibj.Solenoid;

public class SimulationSolenoid extends Solenoid {
	private boolean output;

	public SimulationSolenoid(int channel) {
		super(channel);
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
