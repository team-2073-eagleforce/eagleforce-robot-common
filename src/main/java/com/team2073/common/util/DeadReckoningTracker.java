package com.team2073.common.util;

import java.awt.geom.Point2D;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;
import com.sun.javafx.geom.Vec2d;

import edu.wpi.first.wpilibj.drive.Vector2d;

public class DeadReckoningTracker {
    private Point2D currentRobotLocation = new Point2D.Double();
    private Vec2d currentRobotVector = new Vec2d(0, 0);
    private PigeonIMU gyro;
    private TalonSRX leftMotor;
    private TalonSRX rightMotor;
    private double lastAngle;
    private double lastDistanceLeft;
    private double lastDistanceRight;
    private double currentAcceleration;
    private double currentVelocity;
    private double currentHeading;
    private double xVelocity = 0;
    private double yVelocity = 0;
    private long timeStep = 10;
    private double deltaGyro;
    private double xPosition;
    private double yPosition;
    /**
     * Array to fill with x[0], y[1], and z[2] data. These are in fixed point
     * notation Q2.14. eg. 16384 = 1G
     */
    private short[] accelerometer;
    private double[] gyroAngle;

    private class DriveBase {
        /**
         * In encoder ticks
         */
        public static final double CENTER_TO_WHEEL = 3000;
    }

    /**
     * Tracks Current Location on the field units are in encoder tics and degrees
     * relative to the center of the robot
     *
     * @param gyro
     * @param leftMotor
     * @param rightMotor
     * @param timeStep   in millis
     */
    public DeadReckoningTracker(PigeonIMU gyro, TalonSRX leftMotor, TalonSRX rightMotor, long timeStep) {
        this.gyro = gyro;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.timeStep = timeStep;
        gyro.getBiasedAccelerometer(accelerometer);
        gyro.getYawPitchRoll(gyroAngle);
    }

    public Point2D getCurrentLocation() {
        return currentRobotLocation;
    }

    /**
     * @return a vector of the robots velocity and heading angle <velocity, heading>
     */
    public Vec2d getCurrentRobotVector() {
        return currentRobotVector;
    }

    public void updateCurrentLocation() {
        calculateNewPosition();
        currentRobotLocation.setLocation(xPosition, yPosition);
        currentRobotVector.set(calculateVelocity(), adjustHeading(currentHeading()));
        updateLasts();
    }


    private void calculateNewPosition() {
        updateChangeInGyro();
        xPosition = (average(currentRobotLocation.getX() + changeInX(), integratedXPosition()));
        yPosition = (average(currentRobotLocation.getY() + changeInY(), integratedYPosition()));
    }

    private double currentHeading() {
        gyro.enterCalibrationMode(CalibrationMode.Accelerometer, 10);
        gyro.enterCalibrationMode(CalibrationMode.BootTareGyroAccel, 10);
        return currentHeading += deltaGyro;
    }

    private double calculateXVelocity() {
        xVelocity += (getXAcceleration() * timeStep);
        return xVelocity;
    }

    private double calculateYVelocity() {
        yVelocity += (getYAcceleration() * timeStep);
        return yVelocity;
    }

    private double calculateVelocity() {
        return Math.hypot(xVelocity, yVelocity);
    }

    private double integratedXPosition() {
        double xPosition = (currentRobotLocation.getX() + calculateXVelocity() * timeStep)
                + getXAcceleration() * Math.pow(timeStep, 2);
        return xPosition;
    }

    private double integratedYPosition() {
        double yPosition = (currentRobotLocation.getY() + calculateYVelocity() * timeStep)
                + getYAcceleration() * Math.pow(timeStep, 2);
        return yPosition;
    }

    private double getXAcceleration() {
        return accelerometer[0];
    }

    private double getYAcceleration() {
        return accelerometer[0];
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
        return (changeInLeftDistance() - changeInRightDistance()) / (2 * DriveBase.CENTER_TO_WHEEL);
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
        return chordLength(calculateRightRadius(), deltaGyro) * Math.acos((Math.PI / 180) * ((180 - deltaGyro) / 2));
    }

    private double changeInXLeftSide() {
        return chordLength(calculateLeftRadius(), deltaGyro) * Math.acos((Math.PI / 180) * ((180 - deltaGyro) / 2));
    }

    private double changeInYRightSide() {
        return chordLength(calculateRightRadius(), deltaGyro) * Math.asin((Math.PI / 180) * ((180 - deltaGyro) / 2));
    }

    private double changeInYLeftSide() {
        return chordLength(calculateLeftRadius(), deltaGyro) * Math.asin((Math.PI / 180) * ((180 - deltaGyro) / 2));
    }

    /**
     * @param radius
     * @param angle  in degrees
     * @return the length of the chord through a specified circle;
     */
    private double chordLength(double radius, double angle) {
        return 2 * radius * Math.sin(Math.abs((angle / 2) * (Math.PI / 180)));
    }

    private double calculateLeftRadius() {
        return changeInLeftDistance() / ((deltaGyro / 360) * 2 * Math.PI);
    }

    private double calculateRightRadius() {
        return changeInRightDistance() / ((deltaGyro / 360) * 2 * Math.PI);
    }

}
package com.team2073.common.util;

import java.awt.geom.Point2D;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

public class PathFollower {
	private Point2D currentLocation;
	private PigeonIMU gryo;
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private double lastAngle;
	private double lastDistanceLeft;
	private double lastDistanceRight;

	/**
	 * Tracks Current Location on the field units are in encoder tics and degrees
	 * relative to the center of the robot
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
		currentLocation.setLocation(currentLocation.getX() + (changeInXRightSide()+changeInXLeftSide())/2,
				currentLocation.getY() + (changeInYRightSide() + changeInYLeftSide())/2);
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

	private double changeInXRightSide() {
		return chordLength(calculateRightRadius(), changeInGyro()) * Math.acos(changeInGyro());
	}

	private double changeInXLeftSide() {
		return chordLength(calculateLeftRadius(), changeInGyro()) * Math.acos(changeInGyro());
	}

	private double changeInYRightSide() {
		return chordLength(calculateRightRadius(), changeInGyro()) * Math.asin(changeInGyro());
	}

	private double changeInYLeftSide() {
		return chordLength(calculateLeftRadius(), changeInGyro()) * Math.asin(changeInGyro());
	}

	private double chordLength(double radius, double angle) {
		return 2 * radius * Math.sin(Math.abs((angle / 2) * (Math.PI / 180)));
	}

	private double calculateLeftRadius() {
		return changeInLeftDistance() / ((changeInGyro() / 360) * 2 * Math.PI);
	}

	private double calculateRightRadius() {
		return changeInRightDistance() / ((changeInGyro() / 360) * 2 * Math.PI);
	}

}
package com.team2073.common.util;

import java.awt.geom.Point2D;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

public class PathFollower {
	private Point2D currentLocation;
	private PigeonIMU gryo;
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private double lastAngle;
	private double lastDistanceLeft;
	private double lastDistanceRight;

	/**
	 * Tracks Current Location on the field units are in encoder tics and degrees
	 * relative to the center of the robot
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
		currentLocation.setLocation(currentLocation.getX() + (changeInXRightSide()+changeInXLeftSide())/2,
				currentLocation.getY() + (changeInYRightSide() + changeInYLeftSide())/2);
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

	private double changeInXRightSide() {
		return chordLength(calculateRightRadius(), changeInGyro()) * Math.acos(changeInGyro());
	}

	private double changeInXLeftSide() {
		return chordLength(calculateLeftRadius(), changeInGyro()) * Math.acos(changeInGyro());
	}

	private double changeInYRightSide() {
		return chordLength(calculateRightRadius(), changeInGyro()) * Math.asin(changeInGyro());
	}

	private double changeInYLeftSide() {
		return chordLength(calculateLeftRadius(), changeInGyro()) * Math.asin(changeInGyro());
	}

	private double chordLength(double radius, double angle) {
		return 2 * radius * Math.sin(Math.abs((angle / 2) * (Math.PI / 180)));
	}

	private double calculateLeftRadius() {
		return changeInLeftDistance() / ((changeInGyro() / 360) * 2 * Math.PI);
	}

	private double calculateRightRadius() {
		return changeInRightDistance() / ((changeInGyro() / 360) * 2 * Math.PI);
	}

}
package com.team2073.common.util;

import java.awt.geom.Point2D;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;
import com.sun.javafx.geom.Vec2d;

import edu.wpi.first.wpilibj.drive.Vector2d;

public class DeadReckoningTracker {
	private Point2D currentRobotLocation = new Point2D.Double();
	private Vec2d currentRobotVector = new Vec2d(0, 0);
	private PigeonIMU gyro;
	private TalonSRX leftMotor;
	private TalonSRX rightMotor;
	private double lastAngle;
	private double lastDistanceLeft;
	private double lastDistanceRight;
	private double currentAcceleration;
	private double currentVelocity;
	private double currentHeading;
	private double xVelocity = 0;
	private double yVelocity = 0;
	private long timeStep = 10;
	private double deltaGyro;
	private double xPosition;
	private double yPosition;
	/**
	 * Array to fill with x[0], y[1], and z[2] data. These are in fixed point
	 * notation Q2.14. eg. 16384 = 1G
	 */
	private short[] accelerometer;
	private double[] gyroAngle;

	private class DriveBase {
		/**
		 * In encoder ticks
		 */
		public static final double CENTER_TO_WHEEL = 3000;
	}

	/**
	 * Tracks Current Location on the field units are in encoder tics and degrees
	 * relative to the center of the robot
	 * 
	 * @param gyro
	 * @param leftMotor
	 * @param rightMotor
	 * @param timeStep
	 *            in millis
	 */
	public DeadReckoningTracker(PigeonIMU gyro, TalonSRX leftMotor, TalonSRX rightMotor, long timeStep) {
		this.gyro = gyro;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.timeStep = timeStep;
		gyro.getBiasedAccelerometer(accelerometer);
		gyro.getYawPitchRoll(gyroAngle);
	}

	public Point2D getCurrentLocation() {
		return currentRobotLocation;
	}

	/**
	 * 
	 * @return a vector of the robots velocity and heading angle <velocity, heading>
	 */
	public Vec2d getCurrentRobotVector() {
		return currentRobotVector;
	}

	public void updateCurrentLocation() {
		calculateNewPosition();
		currentRobotLocation.setLocation(xPosition, yPosition);
		currentRobotVector.set(calculateVelocity(), adjustHeading(currentHeading()));
		updateLasts();
	}


	private void calculateNewPosition() {
		updateChangeInGyro();
		xPosition = (average(currentRobotLocation.getX() + changeInX(), integratedXPosition()));
		yPosition = (average(currentRobotLocation.getY() + changeInY(), integratedYPosition()));
	}

	private double currentHeading() {
		gyro.enterCalibrationMode(CalibrationMode.Accelerometer, 10);
		gyro.enterCalibrationMode(CalibrationMode.BootTareGyroAccel, 10);
		return currentHeading += deltaGyro;
	}

	private double calculateXVelocity() {
		xVelocity += (getXAcceleration() * timeStep);
		return xVelocity;
	}

	private double calculateYVelocity() {
		yVelocity += (getYAcceleration() * timeStep);
		return yVelocity;
	}

	private double calculateVelocity() {
		return Math.hypot(xVelocity, yVelocity);
	}

	private double integratedXPosition() {
		double xPosition = (currentRobotLocation.getX() + calculateXVelocity() * timeStep)
				+ getXAcceleration() * Math.pow(timeStep, 2);
		return xPosition;
	}

	private double integratedYPosition() {
		double yPosition = (currentRobotLocation.getY() + calculateYVelocity() * timeStep)
				+ getYAcceleration() * Math.pow(timeStep, 2);
		return yPosition;
	}

	private double getXAcceleration() {
		return accelerometer[0];
	}

	private double getYAcceleration() {
		return accelerometer[0];
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
		return (changeInLeftDistance() - changeInRightDistance()) / (2 * DriveBase.CENTER_TO_WHEEL);
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
		return chordLength(calculateRightRadius(), deltaGyro) * Math.acos((Math.PI / 180) * ((180 - deltaGyro) / 2));
	}

	private double changeInXLeftSide() {
		return chordLength(calculateLeftRadius(), deltaGyro) * Math.acos((Math.PI / 180) * ((180 - deltaGyro) / 2));
	}

	private double changeInYRightSide() {
		return chordLength(calculateRightRadius(), deltaGyro) * Math.asin((Math.PI / 180) * ((180 - deltaGyro) / 2));
	}

	private double changeInYLeftSide() {
		return chordLength(calculateLeftRadius(), deltaGyro) * Math.asin((Math.PI / 180) * ((180 - deltaGyro) / 2));
	}

	/**
	 * 
	 * @param radius
	 * @param angle
	 *            in degrees
	 * @return the length of the chord through a specified circle;
	 */
	private double chordLength(double radius, double angle) {
		return 2 * radius * Math.sin(Math.abs((angle / 2) * (Math.PI / 180)));
	}

	private double calculateLeftRadius() {
		return changeInLeftDistance() / ((deltaGyro / 360) * 2 * Math.PI);
	}

	private double calculateRightRadius() {
		return changeInRightDistance() / ((deltaGyro / 360) * 2 * Math.PI);
	}

}
