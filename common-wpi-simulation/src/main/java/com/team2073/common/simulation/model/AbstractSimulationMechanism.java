package com.team2073.common.simulation.model;

import com.team2073.common.simulation.env.SimulationEnvironment;

public abstract class AbstractSimulationMechanism implements SimulationMechanism {

	protected Runnable whenSolenoidActive;
	protected boolean isSoleniodExtended;

	protected double position;
	protected double velocity;
	protected double acceleration;

	@Override
	public boolean isSolenoidExtended() {
		return isSoleniodExtended;
	}

	@Override
	public void whenSolenoidActive(Runnable function) {
		this.whenSolenoidActive = function;
	}

	@Override
	public void setPosition(double position) {
		this.position = position;
	}

	@Override
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		if(isSolenoidExtended()){
			whenSolenoidActive.run();
		}
	}

	@Override
	public double position() {
		return position;
	}

	@Override
	public double velocity() {
		return velocity;
	}

	@Override
	public double acceleration() {
		return acceleration;
	}

	@Override
	public void updateSolenoid(boolean on) {
		isSoleniodExtended = on;
	}
}
