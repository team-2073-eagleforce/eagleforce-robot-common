package com.team2073.common.controlloop;

public class PidfControlLoop {
	private double p;
	private double i;
	private double d;
	private double f;

	private double output;
	private double maxOutput;
	private double goal;
	private double error;
	private double accumulatedError;
	private double errorVelocity;
	private double lastError;
	private long intervalInMillis;
	private Thread periodic;
	private double position;
	private Double maxIContribution = null;

	/**
	 * @param p
	 * @param i
	 * @param d
	 * @param f if a different input of error is desired, pass in null for talon
	 * @param intervalInMillis
	 * @param maxOutput goal is in units of encoder tics if using a talon
	 */
	public PidfControlLoop(double p, double i, double d, double f, long intervalInMillis, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		if (intervalInMillis <= 0)
			intervalInMillis = 1;
		this.intervalInMillis = intervalInMillis;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					pidCycle();
					try {
						Thread.sleep(PidfControlLoop.this.intervalInMillis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private void pidCycle() {
		error = goal - position;

		output = 0;
		output += f;
		output += p * error;
		if (maxIContribution == null)
			output += i * accumulatedError;
		else
			output += Math.min(i * accumulatedError, maxIContribution);
		output += d * errorVelocity;

		accumulatedError += error;
		errorVelocity = ((error - lastError) / (intervalInMillis));
		error = lastError;

		if (Math.abs(output) >= maxOutput) {
			if (output > 0) {
				output = maxOutput;
			} else {
				output = -maxOutput;
			}
		}
	}

	public double getOutput() {
		return output;
	}

	public void setNewPosition(double position) {
		this.position = position;
	}

	public void startPID(double goal) {
		this.goal = goal;
		if (!periodic.isAlive())
			periodic.start();
	}

	public void stopPID() {
		periodic.interrupt();
		lastError = 0;
		accumulatedError = 0;
		errorVelocity = 0;
	}

	public void updateSetPoint(double newGoal) {
		this.goal = newGoal;
	}

	public double getError() {
		return error;
	}

	public void configMaxIContribution(double maxContribution) {
		this.maxIContribution = maxContribution;
	}

	public void resetAccumulatedError(){
		this.accumulatedError = 0;
	}
}
