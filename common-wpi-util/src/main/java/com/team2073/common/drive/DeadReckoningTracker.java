package com.team2073.common.drive;

import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.Point2D;

public class DeadReckoningTracker {
	private Point2D currentRobotLocation = new Point2D.Double(0, 0);
	private Vector2D currentRobotVector = new Vector2D(0, 0);
	private PigeonIMU gyro;
	private IMotorControllerEnhanced leftMotor;
	private IMotorControllerEnhanced rightMotor;
	private double lastAngle;
	private double lastDistanceLeft;
	private double lastDistanceRight;
	private double currentAcceleration;
	private double currentVelocity;
	private double currentHeading;
	private double xVelocity = 0;
	private double yVelocity = 0;
	private double timeStepInSeconds;
	private double wheelToWheelInInches;
	private double deltaGyro;
	private double xPosition;
	private double yPosition;
	/**
	 * Array to fill with x[0], y[1], and z[2] data. These are in fixed point
	 * notation Q2.14. eg. 16384 = 1G
	 */
	private short[] accelerometer = new short[3];
	private double[] gyroAngle = new double[3];


	/**
	 * Tracks Current Location on the field units are in inches, seconds, and degrees
	 * relative to the center of the robot
	 *
	 * @param gyro
	 * @param leftMotor
	 * @param rightMotor
	 * @param timeStepInSeconds
	 */
	public DeadReckoningTracker(PigeonIMU gyro, IMotorControllerEnhanced leftMotor, IMotorControllerEnhanced rightMotor, double timeStepInSeconds, double wheelToWheelInInches) {
		this.gyro = gyro;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.timeStepInSeconds = timeStepInSeconds;
		this.wheelToWheelInInches = wheelToWheelInInches;
		gyro.getBiasedAccelerometer(accelerometer);
		gyro.getYawPitchRoll(gyroAngle);
	}

	public void calibrationMode() {
//        gyro.enterCalibrationMode(CalibrationMode.Accelerometer, 10);
		gyro.enterCalibrationMode(CalibrationMode.BootTareGyroAccel, 10);
	}

	private double acceltoInchesPerSecondSquared(short accelValue) {
		return (9.8 * (accelValue / 16348.)) * 39.3701 /*inches per meter*/;
	}


	public Point2D getCurrentLocation() {
		return currentRobotLocation;
	}

	/**
	 * @return a vector of the robots velocity and heading angle <velocity, heading>
	 */
	public Vector2D getCurrentRobotVector() {
		return currentRobotVector;
	}

	public void updateCurrentLocation() {
		calculateNewPosition();
		currentRobotLocation.setLocation(xPosition, yPosition);
		currentRobotVector.add(new Vector2D(calculateVelocity(), adjustHeading(currentHeading())).subtract(currentRobotVector));
		updateLasts();
	}


	private void calculateNewPosition() {
		updateChangeInGyro();
		if (deltaGyro == 0) {
			xPosition = integratedXPosition();
			yPosition = integratedYPosition();
		} else {
			xPosition = (average(currentRobotLocation.getX() + changeInX(), integratedXPosition()));
			yPosition = (average(currentRobotLocation.getY() + changeInY(), integratedYPosition()));
		}
	}

	private double currentHeading() {
		return currentHeading += deltaGyro;
	}

	private double calculateXVelocity() {
		xVelocity += (getXAcceleration() * timeStepInSeconds);
		return xVelocity;
	}

	private double calculateYVelocity() {
		yVelocity += (getYAcceleration() * timeStepInSeconds);
		return yVelocity;
	}

	private double calculateVelocity() {
		return Math.hypot(xVelocity, yVelocity);
	}

	// ===================================================================================
	private double integratedXPosition() {
		return currentRobotLocation.getX() + (calculateXVelocity() * timeStepInSeconds);
	}

	private double integratedYPosition() {
		return currentRobotLocation.getY() + (calculateYVelocity() * timeStepInSeconds);
	}

	private double getXAcceleration() {
		return acceltoInchesPerSecondSquared(accelerometer[0]);
	}

	private double getYAcceleration() {
		return acceltoInchesPerSecondSquared(accelerometer[1]);
	}

	private void updateChangeInGyro() {
		deltaGyro = average(gyroAngle[0] - lastAngle, odometryAngle());
	}

	private double changeInX() {
		return average(changeInXRightSide(), changeInXLeftSide());
	}

	private double changeInY() {
		return average(changeInYRightSide(), changeInYLeftSide());
	}

	private double odometryAngle() {
		return (changeInLeftDistance() - changeInRightDistance()) / (wheelToWheelInInches);
	}

	private double average(double... terms) {
		double totalSum = 0;
		for (double val : terms) {
			totalSum += val;
		}
		return (double) (totalSum / terms.length);
	}

	private double changeInLeftDistance() {
		return leftMotor.getSelectedSensorPosition(0) - lastDistanceLeft;
	}

	private double changeInRightDistance() {
		return rightMotor.getSelectedSensorPosition(0) - lastDistanceRight;
	}

	private void updateLasts() {
		lastAngle = gyroAngle[0];
		lastDistanceLeft = leftMotor.getSelectedSensorPosition(0);
		lastDistanceRight = rightMotor.getSelectedSensorPosition(0);
	}

	private double adjustHeading(double heading) {
		return heading % 360;
	}

	private double changeInXRightSide() {
		return chordLength(calculateRightRadius(), deltaGyro) * Math.acos((Math.PI / 180d) * ((180 - deltaGyro) / 2d));
	}

	private double changeInXLeftSide() {
		return chordLength(calculateLeftRadius(), deltaGyro) * Math.acos((Math.PI / 180d) * ((180 - deltaGyro) / 2d));
	}

	private double changeInYRightSide() {
		return chordLength(calculateRightRadius(), deltaGyro) * Math.asin((Math.PI / 180d) * ((180d - deltaGyro) / 2d));
	}

	private double changeInYLeftSide() {
		return chordLength(calculateLeftRadius(), deltaGyro) * Math.asin((Math.PI / 180d) * ((180d - deltaGyro) / 2d));
	}

	/**
	 * @param radius
	 * @param angle  in degrees
	 * @return the length of the chord through a specified circle;
	 */
	private double chordLength(double radius, double angle) {
		return 2 * radius * Math.sin(Math.abs((angle / 2d) * (Math.PI / 180d)));
	}

	private double calculateLeftRadius() {
		double denom = ((deltaGyro / 360d) * 2 * Math.PI);
		if (denom == 0) {
			return 0;
		}
		return changeInLeftDistance() / denom;
	}

	private double calculateRightRadius() {
		double denom = ((deltaGyro / 360d) * 2 * Math.PI);
		if (denom == 0) {
			return 0;
		}
		return changeInRightDistance() / denom;
	}

}