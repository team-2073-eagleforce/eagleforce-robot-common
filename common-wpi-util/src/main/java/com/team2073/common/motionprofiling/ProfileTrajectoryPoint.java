package com.team2073.common.motionprofiling;

public class ProfileTrajectoryPoint {
	private double position;
	private double velocity;
	private double acceleration;
	private double timeStep;
	private double currentTime;
	private double jerk;

	public ProfileTrajectoryPoint(double position, double velocity, double acceleration, double jerk, double timeStep, double currentTime) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.jerk = jerk;
		this.timeStep = timeStep;
		this.currentTime = currentTime;
	}

	public double getPosition() {
		return position;
	}


	public double getVelocity() {
		return velocity;
	}


	public double getAcceleration() {
		return acceleration;
	}


	public double getTimeStep() {
		return timeStep;
	}


	public double getCurrentTime() {
		return currentTime;
	}

	public double getJerk() {
		return jerk;
	}
}
