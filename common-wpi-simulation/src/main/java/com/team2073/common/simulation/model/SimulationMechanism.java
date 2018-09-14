package com.team2073.common.simulation.model;

public interface SimulationMechanism extends SimulationCycleComponent {

	double position();

	double velocity();

	double acceleration();

	void updateVoltage(double voltage);

	void updateSolenoid(boolean on);

	boolean isSolenoidExtended();

	void whenSolenoidActive(Runnable function);

	void setPosition(double position);

	void setVelocity(double velocity);

	void setAcceleration(double acceleration);

	double calculateAcceleration();

	double calculateVelocity(int intervalInMs);

	double calculatePosition(int intervalInMs);

}
