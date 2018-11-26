package com.team2073.common.motionprofiling;

public class ProfileConfiguration {

	private double maxVelocity;
	private double maxAcceleration;
	private double interval;

	public ProfileConfiguration(double maxVelocity, double maxAcceleration, double interval) {
		this.maxVelocity = maxVelocity;
		this.maxAcceleration = maxAcceleration;
		this.interval = interval;
	}

	public double getMaxVelocity() {
		return maxVelocity;
	}

	public double getMaxAcceleration() {
		return maxAcceleration;
	}

	public double getInterval() {
		return interval;
	}

}
