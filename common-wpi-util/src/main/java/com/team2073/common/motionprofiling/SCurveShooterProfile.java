package com.team2073.common.motionprofiling;

public class SCurveShooterProfile {

	private double velocityInitial;
	private double goalVelocity;
	private double vMax;
	private double aMax;
	private double jMax;
	private double dt;

	private double t1;
	private double t2;
	private double t3;

	private double v1;
	private double v2;
	private double v3;

	private double currentTime;

	/**
	 * Generates a series of piecewise functions for Velocity, Acceleration, and Jerk.
	 * @param startingVelocity      Starting velocity of system.
	 * @param goalVelocity          End velocity system must reach.
	 * @param configuration         Configuration with maximum Velocity, Acceleration, Jerk and Interval.
	 */
	public SCurveShooterProfile(double startingVelocity, double goalVelocity, ProfileConfiguration configuration) {
		this.velocityInitial = startingVelocity;
		this.goalVelocity = goalVelocity;
		this.vMax = configuration.getMaxVelocity();
		this.aMax = configuration.getMaxAcceleration();
		this.jMax = configuration.getMaxJerk();
		this.dt = configuration.getInterval();
		generateParameters(vMax, aMax, jMax);
	}

	/**
	 * Generates the necessary time steps corresponding velocity components for the piecewise functions.
	 * @param vMax      Maximum velocity
	 * @param aMax      Maximum acceleration
	 * @param jMax      Maximum jerk
	 */
	public void generateParameters(double vMax, double aMax, double jMax) {
		t1 = aMax / jMax;
		v1 = (jMax * Math.pow(t1, 2)) / 2d;
		t2 = (vMax - velocityInitial - (2 * v1)) / aMax + t1;
		v2 = aMax * (t2-t1) + v1;
		t3 = t1 + t2;
	}

	/**
	 * Generates the next Trajectory Point in the profile.
	 * @param interval  Time that has elapsed from previous point.
	 * @return          New ProfileTrajectoryPoint with current Velocity, Acceleration, Jerk, Interval and Current Time.
	 */

	public ProfileTrajectoryPoint nextPoint(double interval) {
		currentTime += interval;
		if(currentTime >= t3) {
			return new ProfileTrajectoryPoint(vMax, 0, 0, interval, currentTime);
		}
		return new ProfileTrajectoryPoint(calcVelocity(), calcAcceleration(), calcJerk(),interval, currentTime);
	}

	public double calcVelocity() {
		if(isBetweenTimes(0, t1)){
			return (jMax * Math.pow(currentTime, 2)) / 2 + velocityInitial;
		} else if(isBetweenTimes(t1, t2)) {
			return aMax * (currentTime - t1) + v1 + velocityInitial;
		} else {
			return v2 + (-jMax * Math.pow((currentTime- t2), 2)) /2 + aMax * (currentTime - t2);
		}
	}

	public double calcAcceleration() {
		if(isBetweenTimes(0, t1)){
			return jMax * currentTime;
		} else if(isBetweenTimes(t1, t2)) {
			return aMax;
		} else {
			return (-jMax * (currentTime - t2)) + aMax;
		}
	}

	public double calcJerk() {
		if(isBetweenTimes(0, t1)){
			return jMax;
		} else if(isBetweenTimes(t1, t2)) {
			return 0;
		} else {
			return -jMax;
		}
	}

	private boolean isBetweenTimes(double startTime, double endTime) {
		return currentTime >= startTime && currentTime <= endTime;
	}

}
