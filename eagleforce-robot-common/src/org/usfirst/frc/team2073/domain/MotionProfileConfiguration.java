package org.usfirst.frc.team2073.domain;

public class MotionProfileConfiguration {
	private double maxVel;
	private double endDistance;
	private int interval;
	private double maxAcc;
	private boolean isVelocityOnly;
	private boolean isForwards;
	
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
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public double getMaxAcc() {
		return maxAcc;
	}
	
	public void setMaxAcc(double maxAcc) {
		this.maxAcc = maxAcc;
	}
}
