package com.team2073.common.motionprofiling;

import org.apache.commons.math3.exception.OutOfRangeException;

import static java.lang.Math.pow;

public class SCurveProfileGenerator {

	private final double goalPosition;
	private final double vMax;
	private final double aMax;
	private final double jMax;

	private final double t1;
	private final double t2;
	private final double t3;
	private final double t4;
	private final double t5;
	private final double t6;
	private final double t7;

	private final double wT1;
	private final double wT2;
	private final double wT3;
	private final double wT4;
	private final double wT5;
	private final double wT6;
	private final double wT7;

	private final double p1;
	private final double p2;
	private final double p3;
	private final double p4;
	private final double p5;
	private final double p6;

	private final double v1;
	private final double v2;
	private final double v5;
	private final double v6;


	private double currentPosition;
	private double currentVelocity;
	private double currentAcceleration;
	private double currentJerk;


	private double currentTime;


	/**
	 * Creates a series of 7-segment piecewise functions for each of Position, Velocity, Acceleration, and Jerk. <br/>
	 * On construction, calculates each point of domain change for the various piecewise sections. \(ax^2 + bx + c\)
	 *
	 * @param goalPosition
	 * @param vMax
	 * @param aMax
	 * @param jMax
	 */
	public SCurveProfileGenerator(double goalPosition, double vMax, double aMax, double jMax) {
		this.goalPosition = goalPosition;

		this.vMax = vMax;

		this.aMax = aMax;
		this.jMax = jMax;

		t1 = aMax / jMax;
		wT1 = t1;
		t2 = t1 + vMax / aMax - aMax / jMax;
		wT2 = t2 - t1;
		t3 = t2 + wT1;
		wT3 = wT1;
		t4 = t3 + ((goalPosition - 2d * ((jMax * pow(wT1, 3) / 3d) + (.5 * aMax * pow(wT2, 2)) +
				(.5 * aMax * wT1 * wT2) + (wT3 * (vMax - .5 * aMax * wT1)))) / vMax);
		wT4 = t4 - t3;
		t5 = t4 + wT1;
		wT5 = wT1;
		t6 = t5 + wT2;
		wT6 = wT2;
		t7 = t6 + wT3;
		wT7 = wT3;

		p1 = (jMax / 6d) * pow(wT1, 3);
		p2 = p1 + (.5 * aMax * pow(wT2, 2)) + (.5 * aMax * wT1 * wT2);
		p3 = p2 + p1 + (wT3 * (vMax - .5 * aMax * wT1));
		p4 = p3 + wT4 * vMax;
		p5 = p4 + p1 + (wT3 * (vMax - .5 * aMax * wT1));
		p6 = p5 + (.5 * aMax * pow(wT2, 2)) + (.5 * aMax * wT1 * wT2);

		v1 = .5 * aMax * wT1;
		v2 = v1 + aMax * wT2;
		v5 = vMax - .5 * aMax * wT1;
		v6 = v5 - aMax * wT2;


	}

	public ProfileTrajectoryPoint nextPoint(double interval) {
		currentTime += interval;
		return new ProfileTrajectoryPoint(calcPosition(), calcVelocity(), calcAcceleration(), calcJerk(), interval, currentTime);
	}


	/**
	 * Derived from integrating the velocity function on the same time interval, assuming a constant jerk value for each segment.
	 * <p>
	 * Generally follows the pattern of \(ax^2 + bx + c\)
	 *
	 * @return The position corresponding with the current time segment.
	 */
	private double calcPosition() {
		double position;
		if (isBetweenTimes(0, t1)) {
			position = (jMax / 6) * pow(currentTime, 3);
		} else if (isBetweenTimes(t1, t2)) {
			position = p1 + v1 * (currentTime - t1) + (aMax / 2) * pow(currentTime - t1, 2);
		} else if (isBetweenTimes(t2, t3)) {
			position = p2 + v2 * (currentTime - t2) + (aMax / 2) * pow(currentTime - t2, 2) - (jMax / 6) * pow(currentTime - t2, 3);
		} else if (isBetweenTimes(t3, t4)) {
			position = p3 + vMax * (currentTime - t3);
		} else if (isBetweenTimes(t4, t5)) {
			position = p4 + vMax * (currentTime - t4) - (jMax / 6) * pow(currentTime - t4, 3);
		} else if (isBetweenTimes(t5, t6)) {
			position = p5 + v5 * (currentTime - t5) - (aMax / 2) * pow(currentTime - t5, 2);
		} else if (isBetweenTimes(t6, t7)) {
			position = p6 + v6 * (currentTime - t6) - (aMax / 2) * pow(currentTime - t6, 2) + (jMax / 6) * pow(currentTime - t6, 3);
		} else if (currentTime >= t7) {
			position = goalPosition;
		} else {
			throw new OutOfRangeException(currentTime, 0d, t7);
		}
		return position;
	}

	/**
	 * Derived from integrating the Acceleration function on the same time interval, assuming a constant jerk value for each segment.
	 *
	 * @return The position corresponding with the current time segment.
	 */
	private double calcVelocity() {
		double velocity;
		if (isBetweenTimes(0, t1)) {
			velocity = (jMax / 2) * pow(currentTime, 2);
		} else if (isBetweenTimes(t1, t2)) {
			velocity = v1 + aMax * (currentTime - t1);
		} else if (isBetweenTimes(t2, t3)) {
			velocity = v2 + aMax * (currentTime - t2) - (jMax / 2) * pow(currentTime - t2, 2);
		} else if (isBetweenTimes(t3, t4)) {
			velocity = vMax;
		} else if (isBetweenTimes(t4, t5)) {
			velocity = vMax - (jMax / 2) * pow(currentTime - t4, 2);
		} else if (isBetweenTimes(t5, t6)) {
			velocity = v5 - aMax * (currentTime - t5);
		} else if (isBetweenTimes(t6, t7)) {
			velocity = v6 - aMax * (currentTime - t6) + (jMax / 2) * pow(currentTime - t6, 2);
		} else if (currentTime >= t7) {
			velocity = 0;
		} else {
			throw new OutOfRangeException(currentTime, 0, t7);
		}
		return velocity;
	}

	/**
	 * Derived from integrating the Jerk function on the same time interval, assuming a constant jerk value for each segment.
	 *
	 * @return The Acceleration corresponding with the current time segment.
	 */
	private double calcAcceleration() {
		double acceleration;
		if (isBetweenTimes(0, t1)) {
			acceleration = jMax * currentTime;
		} else if (isBetweenTimes(t1, t2)) {
			acceleration = aMax;
		} else if (isBetweenTimes(t2, t3)) {
			acceleration = aMax - jMax * (currentTime - t2);
		} else if (isBetweenTimes(t3, t4)) {
			acceleration = 0;
		} else if (isBetweenTimes(t4, t5)) {
			acceleration = -jMax * (currentTime - t4);
		} else if (isBetweenTimes(t5, t6)) {
			acceleration = -aMax;
		} else if (isBetweenTimes(t6, t7)) {
			acceleration = -aMax + jMax * (currentTime - t6);
		} else if (currentTime >= t7) {
			acceleration = 0;
		} else {
			throw new OutOfRangeException(currentTime, 0, t7);
		}
		return acceleration;
	}

	/**
	 * @return The Jerk corresponding with the current time segment.
	 */
	private double calcJerk() {
		double jerk;
		if (isBetweenTimes(0, t1)) {
			jerk = jMax;
		} else if (isBetweenTimes(t1, t2)) {
			jerk = 0;
		} else if (isBetweenTimes(t2, t3)) {
			jerk = -jMax;
		} else if (isBetweenTimes(t3, t4)) {
			jerk = 0;
		} else if (isBetweenTimes(t4, t5)) {
			jerk = -jMax;
		} else if (isBetweenTimes(t5, t6)) {
			jerk = 0;
		} else if (isBetweenTimes(t6, t7)) {
			jerk = jMax;
		} else if (currentTime >= t7) {
			jerk = 0;
		} else {
			throw new OutOfRangeException(currentTime, 0, t7);
		}
		return jerk;
	}

	private boolean isBetweenTimes(double startTime, double endTime) {
		return currentTime > startTime && currentTime < endTime;
	}


	public double getTotalTime() {
		return t7;
	}

	public double currentPosition() {
		return calcPosition();
	}

	public double currentVelocity() {
		return calcVelocity();
	}

	public double currentAcceleration() {
		return calcAcceleration();
	}
}
