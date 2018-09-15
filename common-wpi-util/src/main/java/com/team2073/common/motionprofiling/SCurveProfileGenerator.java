package com.team2073.common.motionprofiling;

import org.apache.commons.math3.exception.OutOfRangeException;

import static java.lang.Math.pow;

public class SCurveProfileGenerator {

	private final double goalPosition;
	private final double mV;
	private final double aMax;
	private final double aAvg;
	private final double jMax;

	private final double t1;
	private final double t2;
	private final double tAccel;
	private final double t3;
	private final double t4;
	private final double tTotal;

	private final double tAtMaxVel;

	private final double p1;
	private final double p2;
	private final double pAccel;
	private final double pAtMaxVel;
	private final double v1;
	private final double v2;
	private double p3;
	private double p4;
	private double v3;
	private double v4;

	private double currentTime;


	public SCurveProfileGenerator(double goalPosition, double maxVelocity, double maxAcceleration, double averageAccelerarion) {
		this.goalPosition = goalPosition;

		this.mV = maxVelocity;

		this.aMax = maxAcceleration;
		this.aAvg = averageAccelerarion;


		jMax = (pow(aMax, 2) * averageAccelerarion) / (maxVelocity * (aMax - averageAccelerarion));

		t1 = aMax / jMax;

		t2 = maxVelocity / averageAccelerarion - t1;

		tAccel = maxVelocity / averageAccelerarion;

		v1 = (jMax / 2) * pow(t1, 2);

		v2 = v1 + aMax * (t2 - t1);

		p1 = ((jMax / 6) * pow(t1, 3));

		p2 = p1 + v1 * (t2-t1) + (aMax / 2) * pow(t2-t1, 2);

		pAccel = p2 + v2 * (tAccel - t2) + (aMax / 2) * pow(tAccel - t2, 2) - (jMax / 6) * pow(tAccel - t2, 3);

		tAtMaxVel = tAccel + (goalPosition - 2 * pAccel) / maxVelocity;

		t3 = tAtMaxVel + tAccel - t2;

		t4 = t3 + t2 - t1;

		tTotal = t4 + t1;

		pAtMaxVel = pAccel + mV * (tAtMaxVel - tAccel);

		v3 = mV - (jMax / 2) * pow(t3 - tAtMaxVel, 2);

		v4 = v3 - aMax * (t4 - t3);

		p3 = pAtMaxVel + mV * (t3 - tAtMaxVel) - (jMax / 6) * pow(t3 - tAtMaxVel, 3);

		p4 = p3 + v3 * (t4 - t3) - (aMax / 2) * pow(t4 - t3, 2);


	}


	public ProfileTrajectoryPoint nextPoint(double interval) {
		currentTime += interval;
		return new ProfileTrajectoryPoint(calcPosition(), calcVelocity(), calcAcceleration(), calcJerk(), interval, currentTime);
	}

	private double calcPosition() {
		double position;
		if (isBetweenTimes(0, t1)) {
			position = (jMax / 6) * pow(currentTime, 3);
		} else if (isBetweenTimes(t1, t2)) {
			position = p1 + v1 * (currentTime - t1) + (aMax / 2) * pow(currentTime - t1, 2);
		} else if (isBetweenTimes(t2, tAccel)) {
			position = p2 + v2 * (currentTime - t2) + (aMax / 2) * pow(currentTime - t2, 2) - (jMax / 6) * pow(currentTime - t2, 3);
		} else if (isBetweenTimes(tAccel, tAtMaxVel)) {
			position = pAccel + mV * (currentTime - tAccel);
		} else if (isBetweenTimes(tAtMaxVel, t3)) {
			position = pAtMaxVel + mV * (currentTime - tAtMaxVel) - (jMax / 6) * pow(currentTime - tAtMaxVel, 3);
		} else if (isBetweenTimes(t3, t4)) {
			position = p3 + v3 * (currentTime - t3) - (aMax / 2) * pow(currentTime - t3, 2);
		} else if (isBetweenTimes(t4, tTotal)) {
			position = p4 + v4 * (currentTime - t4) - (aMax / 2) * pow(currentTime - t4, 2) + (jMax / 6) * pow(currentTime - t4, 3);
		} else {
			throw new OutOfRangeException(currentTime, 0, tTotal);
		}

		return position;
	}


	private double calcVelocity() {
		double velocity;
		if (isBetweenTimes(0, t1)) {
			velocity = (jMax / 2) * pow(currentTime, 2);
		} else if (isBetweenTimes(t1, t2)) {
			velocity = v1 + aMax * (currentTime - t1);
		} else if (isBetweenTimes(t2, tAccel)) {
			velocity = v2 + aMax * (currentTime - t2) - (jMax / 2) * pow(currentTime - t2, 2);
		} else if (isBetweenTimes(tAccel, tAtMaxVel)) {
			velocity = mV;
		} else if (isBetweenTimes(tAtMaxVel, t3)) {
			velocity = mV - (jMax / 2) * pow(currentTime - tAtMaxVel, 2);
		} else if (isBetweenTimes(t3, t4)) {
			velocity = v3 - aMax * (currentTime - t3);
		} else if (isBetweenTimes(t4, tTotal)) {
			velocity = v4 - aMax * (currentTime - t4) + (jMax / 6) * pow(currentTime - t4, 3);
		} else {
			throw new OutOfRangeException(currentTime, 0, tTotal);
		}
		return velocity;
	}

	private double calcAcceleration() {
		double acceleration;
		if (isBetweenTimes(0, t1)) {
			acceleration = jMax * currentTime;
		} else if (isBetweenTimes(t1, t2)) {
			acceleration = aMax;
		} else if (isBetweenTimes(t2, tAccel)) {
			acceleration = aMax - jMax * (currentTime - t2);
		} else if (isBetweenTimes(tAccel, tAtMaxVel)) {
			acceleration = 0;
		} else if (isBetweenTimes(tAtMaxVel, t3)) {
			acceleration = -jMax * (currentTime - tAtMaxVel);
		} else if (isBetweenTimes(t3, t4)) {
			acceleration = -aMax;
		} else if (isBetweenTimes(t4, tTotal)) {
			acceleration = -aMax + jMax * (currentTime - t4);
		} else {
			throw new OutOfRangeException(currentTime, 0, tTotal);
		}
		return acceleration;
	}

	private double calcJerk() {
		double jerk;
		if (isBetweenTimes(0, t1)) {
			jerk = jMax;
		} else if (isBetweenTimes(t1, t2)) {
			jerk = 0;
		} else if (isBetweenTimes(t2, tAccel)) {
			jerk = -jMax;
		} else if (isBetweenTimes(tAccel, tAtMaxVel)) {
			jerk = 0;
		} else if (isBetweenTimes(tAtMaxVel, t3)) {
			jerk = -jMax;
		} else if (isBetweenTimes(t3, t4)) {
			jerk = 0;
		} else if (isBetweenTimes(t4, tTotal)) {
			jerk = jMax;
		} else {
			throw new OutOfRangeException(currentTime, 0, tTotal);
		}
		return jerk;
	}

	private boolean isBetweenTimes(double startTime, double endTime) {
		return currentTime > startTime && currentTime < endTime;
	}


	public double getTotalTime() {
		return tTotal;
	}
}
