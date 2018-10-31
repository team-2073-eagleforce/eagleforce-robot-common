package com.team2073.common.drive;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class PathSteering extends DeadReckoningTracker {
	private Vector2D currentRobotVelocity = new Vector2D(0, 0);
	private Vector2D desiredRobotVelocity = new Vector2D(0, 0);
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private PigeonIMU gyro;

	public PathSteering(PigeonIMU gyro, TalonSRX leftMotor, TalonSRX rightMotor) {
		super(null, null, null, null, null, 10, 25.5);
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.gyro = gyro;
	}

	/**
	 * angles are in standard position and in degrees, velocity is in
	 * encoderTics/100ms
	 */
	public void updateCurrentRobotVelocity() {
		double avgVelocity = (leftMotor.getSelectedSensorVelocity(0) + rightMotor.getSelectedSensorVelocity(0)) / 2;
		double robotAbsoluteAngle = gyro.getAbsoluteCompassHeading();
		double x = avgVelocity * Math.cos(degreesToRadians(robotAbsoluteAngle));
		double y = avgVelocity * Math.sin(degreesToRadians(robotAbsoluteAngle));
		currentRobotVelocity.add(new Vector2D(x, y).subtract(currentRobotVelocity));
	}

	private double radiansToDegrees(double radians) {
		return (radians * (180 / Math.PI));
	}

	private double degreesToRadians(double degrees) {
		return (degrees * (Math.PI / 180));
	}

	/**
	 * Velocity in units of encoderTics/100ms and heading in degrees
	 */
	public void setDesiredVelocity(double velocity, double heading) {
		double x = velocity * Math.cos(degreesToRadians(heading));
		double y = velocity * Math.sin(degreesToRadians(heading));
		desiredRobotVelocity.add(new Vector2D(x, y).subtract(desiredRobotVelocity));
	}
	
	private void steeringCalc() {
		Vector2D steering = new Vector2D(desiredRobotVelocity.getX() - currentRobotVelocity.getX(), desiredRobotVelocity.getY() - currentRobotVelocity.getY());
	}
	
	private void steer() {
		
	}
}

