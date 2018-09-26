package com.team2073.common.controlloop;

import com.team2073.common.motionprofiling.ProfileTrajectoryPoint;
import com.team2073.common.util.ConversionUtil;

import java.util.concurrent.Callable;

public class MotionProfileControlloop {
	private double p;
	private double d;
	private double kv;
	private double ka;

	private double output;
	private double maxOutput;
	private double error;
	private double lastError;
	private double interval;
	private Thread periodic;
	private double position;
	private ProfileTrajectoryPoint currentPoint;
	private Callable<ProfileTrajectoryPoint> dataPointUpdater;
	private Callable<Double> positionUpdater;

	/**
	 *
	 * @param p proportional gain
	 * @param d derivative gain
	 * @param kv velocity constant
	 * @param ka acceleration constant
	 * @param interval in seconds
	 * @param maxOutput
	 *
	 */
	public MotionProfileControlloop(double p, double d, double kv, double ka, double interval, double maxOutput) {
		this.p = p;
		this.d = d;
		this.kv = kv;
		this.ka = ka;
		this.maxOutput = maxOutput;
		if (interval <= 0)
			interval = .01;
		this.interval = interval;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						MotionProfileControlloop.this.currentPoint = dataPointUpdater.call();
						MotionProfileControlloop.this.position = positionUpdater.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
					pidCycle();
					try {
						Thread.sleep(ConversionUtil.secondsToMs(MotionProfileControlloop.this.interval));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private void pidCycle() {
		error = currentPoint.getPosition() - position;

		output = 0;
		output += p * error;
		output += d * ((error - lastError) / interval - currentPoint.getVelocity());
		output += kv * currentPoint.getVelocity();
		output += ka * currentPoint.getAcceleration();

		error = lastError;

		if (Math.abs(output) >= maxOutput) {
			if (output > 0) {
				output = maxOutput;
			} else {
				output = -maxOutput;
			}
		}
	}

	public void dataPointCallable(Callable<ProfileTrajectoryPoint> desiredPoint) {
		this.dataPointUpdater = desiredPoint;
	}

	public void updatePosition(Callable<Double> returnsPosition) {
		this.positionUpdater = returnsPosition;
	}

	public double getOutput() {
		return output;
	}

	public void start() {
		if (!periodic.isAlive())
			periodic.start();
	}

	public void stop() {
		periodic.interrupt();
	}

}
