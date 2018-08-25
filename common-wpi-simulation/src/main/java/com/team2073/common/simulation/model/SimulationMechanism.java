package com.team2073.common.simulation.model;

public interface SimulationMechanism extends SimulationCycleComponent {

	double position();

	double velocity();

	void updateVoltage(double voltage);


}
