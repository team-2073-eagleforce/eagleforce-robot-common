package com.team2073.common.controlloop;

import com.team2073.common.motionprofiling.ProfileTrajectoryPoint;

import java.util.concurrent.Callable;

import static com.team2073.common.controlloop.PidfControlLoop.PositionSupplier;

public class MotionProfileControlloop {
	private double p;
	private double d;
	private double kv;
	private double ka;

	private double output;
	private double maxOutput;
	private double error;
	private double lastError;
	private double position;
	private ProfileTrajectoryPoint currentPoint;
	private Callable<ProfileTrajectoryPoint> dataPointUpdater;
	private PositionSupplier positionUpdater;

	/**
	 * @param p         proportional gain
	 * @param d         derivative gain
	 * @param kv        velocity constant
	 * @param ka        acceleration constant
	 * @param maxOutput
	 */
	public MotionProfileControlloop(double p, double d, double kv, double ka, double maxOutput) {
		this.p = p;
		this.d = d;
		this.kv = kv;
		this.ka = ka;
		this.maxOutput = maxOutput;
	}

	public void update(double interval) {
		try {
			this.currentPoint = dataPointUpdater.call();
			this.position = positionUpdater.currentPosition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pidCycle(interval);
	}

	private void pidCycle(double interval) {
		error = currentPoint.getPosition() - position;

		output = 0;
		output += p * error;
		output += d * ((error - lastError) / interval - currentPoint.getVelocity());
		output += kv * currentPoint.getVelocity();
		output += ka * currentPoint.getAcceleration();


		if (Math.abs(output) >= maxOutput) {
			if (output > 0) {
				output = maxOutput;
			} else {
				output = -maxOutput;
			}
		}

		lastError = error;
	}

	public void dataPointCallable(Callable<ProfileTrajectoryPoint> desiredPoint) {
		this.dataPointUpdater = desiredPoint;
	}

	public void updatePosition(PositionSupplier returnsPosition) {
		this.positionUpdater = returnsPosition;
	}

	public double getOutput() {
		return output;
	}

}
