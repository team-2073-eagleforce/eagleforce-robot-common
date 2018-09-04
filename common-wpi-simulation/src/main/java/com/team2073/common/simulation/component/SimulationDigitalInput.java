package com.team2073.common.simulation.component;

import com.team2073.common.simulation.model.SimulationMechanism;
import edu.wpi.first.wpilibj.DigitalInput;

public class SimulationDigitalInput extends DigitalInput {

	private SimulationMechanism mechanism;
	private double location;
	private double offset;

	public SimulationDigitalInput(int channel) {
		super(channel);
	}

	public void setMechanism(SimulationMechanism mechanism) {
		this.mechanism = mechanism;
	}

	public void setLocation(double location) {
		this.location = location;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	@Override
	public boolean get() {
		return mechanism.position() > location - offset && mechanism.position() < location + offset;
	}
}
