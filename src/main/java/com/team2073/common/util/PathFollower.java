package com.team2073.common.util;

import java.awt.geom.Point2D;

import javax.swing.text.AbstractDocument.LeafElement;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.interfaces.Gyro;

public class PathFollower {
	private Point2D currentLocation;
	private PigeonIMU gryo;
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private double lastAngle;
	private double lastDistanceLeft;
	private double lastDistanceRight;
	
	
	/**
	 * Tracks Current Location on the field
	 * units are in encoder tics and degrees relative to the right wheels of the robot
	 * 
	 * @param gyro
	 * @param leftMotor
	 * @param rightMotor
	 */
	public PathFollower(PigeonIMU gyro, TalonSRX leftMotor, TalonSRX rightMotor) {
		this.gryo = gyro;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	public Point2D getCurrentLocation() {
		return currentLocation;
	}
	
	public void updateCurrentLocation() {
		currentLocation.setLocation(currentLocation.getX() + changeInX(), currentLocation.getY() + changeInY());
		updateLasts();
	}
	
	
	
	private double changeInGyro() {
		return gryo.getCompassHeading() - lastAngle;
	}
	
	private double changeInLeftDistance() {
		return leftMotor.getSelectedSensorPosition(0) - lastDistanceLeft;
	}
	
	private double changeInRightDistance() {
		return rightMotor.getSelectedSensorPosition(0) - lastDistanceRight;
	}
	
	private void updateLasts() {
		lastAngle = gryo.getAbsoluteCompassHeading();
		lastDistanceLeft = leftMotor.getSelectedSensorPosition(0);
		lastDistanceRight = rightMotor.getSelectedSensorPosition(0);
	}
	
	private double changeInX() {
		return chordLength(calculateRightRadius(), changeInGyro())*Math.acos(changeInGyro());
	}
	
	private double changeInY() {
		return chordLength(calculateRightRadius(), changeInGyro())*Math.asin(changeInGyro());
	}
	
	private double chordLength(double radius, double angle) {
		return 2*radius*Math.sin(Math.abs((angle/2)*(Math.PI/180)));
	}
	
	private double calculateLeftRadius() {
		return changeInLeftDistance()/((changeInGyro()/360)*2*Math.PI);
	}
	
	private double calculateRightRadius() {
		return changeInRightDistance()/((changeInGyro()/360)*2*Math.PI);
	}

}
