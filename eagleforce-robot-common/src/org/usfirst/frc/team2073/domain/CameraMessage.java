package org.usfirst.frc.team2073.domain;

public class CameraMessage {
	private double angleToTarget = 0;
	private double distanceToTarget = 0;
	private double timeOfImage = 0;
	private boolean isTracking = false;
	
	public double getAngleToTarget() {
		return angleToTarget;
	}
	
	public void setAngleToTarget(double angleToTarget) {
		this.angleToTarget = angleToTarget;
	}
	
	public double getDistanceToTarget() {
		return distanceToTarget;
	}
	
	public void setDistanceToTarget(double distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}
	
	public double getTimeOfImage() {
		return timeOfImage;
	}
	
	public void setTimeOfImage(double timeOfImage) {
		this.timeOfImage = timeOfImage;
	}
	
	public boolean isTracking() {
		return isTracking;
	}
	
	public void setTracking(boolean isTracking) {
		this.isTracking = isTracking;
	}
}
