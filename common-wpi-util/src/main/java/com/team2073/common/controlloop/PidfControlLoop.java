package com.team2073.common.controlloop;

import java.util.concurrent.Callable;

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
	private Callable<Boolean> fCondition;

	public PidfControlLoop(double p, double i, double d, double f, long intervalInMillis, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		if (intervalInMillis <= 0)
			intervalInMillis = 10;
		this.intervalInMillis = intervalInMillis;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						pidCycle();
						Thread.sleep(PidfControlLoop.this.intervalInMillis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private void pidCycle() throws Exception {
		error = goal - position;

		output = 0;

		if(fCondition == null || fCondition.call()){
			output += f;
		}

		accumulatedError += error * (intervalInMillis / 1000d);
		errorVelocity = ((error - lastError) / (intervalInMillis / 1000d));

		output += p * error;
		if (maxIContribution == null)
			output += i * accumulatedError;
		else
			output += Math.min(i * accumulatedError, maxIContribution);
		output += d * errorVelocity;

		lastError = error;

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

	/**
	 * The F gain will only be applied if this condition is true, if not specified, F gain will always be used.
	 */
	public void useFCondition(Callable<Boolean> fCondition){
		this.fCondition = fCondition;
	}
}
