package com.team2073.common.dev.simulation.io;

// TODO: Move to common
public class FakeTalon {
	
	public double position;
	
	public FakeTalon() {
		
	}

	public FakeTalon(double position) {
		this.position = position;
	}
	
	public void set(double speed) {
		// TODO: Throw this on it's own thread (make sure to synchronize)
		position += speed;
	}
}
