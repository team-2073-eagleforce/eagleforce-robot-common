package com.team2073.common.motionprofiling;

import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;

public class MotionProfileConfiguration {
	private double maxVel;
	private double endDistance;
	private TrajectoryDuration interval;
	private double maxAcc;
	private boolean isVelocityOnly;
	private boolean isForwards;
	private int intervalVal;
	
	public int getIntervalVal() {
		return intervalVal;
	}

	public void setIntervalVal(int intervalVal) {
		this.intervalVal = intervalVal;
	}

	public boolean isForwards() {
		return isForwards;
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
	
	public TrajectoryDuration getInterval() {
		return interval;
	}
	
	public void setInterval(TrajectoryDuration interval) {
		this.interval = interval;
	}
	
	public double getMaxAcc() {
		return maxAcc;
	}
	
	public void setMaxAcc(double maxAcc) {
		this.maxAcc = maxAcc;
	}
}
