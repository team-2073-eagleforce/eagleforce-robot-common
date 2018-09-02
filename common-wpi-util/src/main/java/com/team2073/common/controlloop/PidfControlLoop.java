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
	private long intervalInMilis;
	private Thread periodic;
	private double position;

	/**
	 * 
	 * @param p
	 * @param i
	 * @param d
	 * @param f
	 *            if a different input of error is desired, pass in null for talon
	 * @param intervalInMilis
	 * @param maxOutput
	 *            <p>
	 *            goal is in units of encoder tics if using a talon
	 */
	public PidfControlLoop(double p, double i, double d, double f, long intervalInMilis, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		if (intervalInMilis <= 0)
			intervalInMilis = 1;
		this.intervalInMilis = intervalInMilis;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					pidCycle();
					try {
						Thread.sleep(PidfControlLoop.this.intervalInMilis);
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
		output += i * accumulatedError;
		output += d * errorVelocity;

		accumulatedError += error;
		errorVelocity = (double) ((error - lastError) / (intervalInMilis));
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
		if(!periodic.isAlive())
			periodic.start();
	}

	public void stopPID() {
		periodic.interrupt();
	}

}
