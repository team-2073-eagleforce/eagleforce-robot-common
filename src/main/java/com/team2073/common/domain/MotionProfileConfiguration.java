package com.team2073.common.domain;

import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;

public class MotionProfileConfiguration {
	private double maxVel;
	private double endDistance;
	private TrajectoryDuration interval;
	private int intervalVal;
	private double maxAcc;
	private boolean isVelocityOnly;
	private boolean isForwards;

	public TrajectoryDuration getInterval() {
		return interval;
	}

	public boolean isForwards() {
		return isForwards;
	}

	public int getIntervalVal() {
		return intervalVal;
	}

	public void setIntervalVal(int intervalVal) {
		this.intervalVal = intervalVal;
	}

	public void setInterval(TrajectoryDuration interval) {
		this.interval = interval;
	}

	public void setForwards(boolean isForwards) {
		this.isForwards = isForwards;
	}

	public boolean isVelocityOnly() {
		return isVelocityOnly;
	}

	public void setVelocityOnly(boolean isVelocityOnly) {
		this.isVelocityOnly = isVelocityOnly;
	}

	public double getMaxVel() {
		return maxVel;
	}

	public void setMaxVel(double maxVel) {
		this.maxVel = maxVel;
	}

	public double getEndDistance() {
		return endDistance;
	}

	public void setEndDistance(double endDistance) {
		this.endDistance = endDistance;
	}

	public double getMaxAcc() {
		return maxAcc;
	}

	public void setMaxAcc(double maxAcc) {
		this.maxAcc = maxAcc;
	}
}
