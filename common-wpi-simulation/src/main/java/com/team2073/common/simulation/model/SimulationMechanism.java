package com.team2073.common.simulation.model;

public interface SimulationMechanism extends SimulationCycleComponent {

	double position();

	double velocity();

	double acceleration();

	void updateVoltage(double voltage);

	void updateSolenoid(boolean on);

	boolean solenoidPosition();


}
