package com.team2073.common.controlLoops;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.sun.javafx.geom.Vec2d;
import com.team2073.common.util.PathFollower;

public class PathSteering extends PathFollower {
	private Vec2d currentRobotVelocity = new Vec2d(0, 0);
	private Vec2d desiredRobotVelocity = new Vec2d(0, 0);
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private PigeonIMU gyro;

	public PathSteering(PigeonIMU gyro, TalonSRX leftMotor, TalonSRX rightMotor) {
		super(gyro, leftMotor, rightMotor);
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.gyro = gyro;
		currentRobotVelocity.set(0, 0);
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
		currentRobotVelocity.set(x, y);
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
		desiredRobotVelocity.set(x, y);
	}
	
	private void steeringCalc() {
		Vec2d steering = new Vec2d(desiredRobotVelocity.x - currentRobotVelocity.x, desiredRobotVelocity.y - currentRobotVelocity.y);
	}
	
	private void steer() {
		
	}
}

