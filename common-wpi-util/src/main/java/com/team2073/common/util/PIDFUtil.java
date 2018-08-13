package com.team2073.common.util;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class PIDFUtil {
	private double p;
	private double i;
	private double d;
	private double f;

	private double output;
	private double maxOutput;
	private TalonSRX talon;
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
	 * @param talon
	 *            if a different input of error is desired, pass in null for talon
	 * @param intervalInMilis
	 * @param maxOutput
	 *            <p>
	 *            goal is in units of encoder tics if using a talon
	 */
	public PIDFUtil(double p, double i, double d, double f, TalonSRX talon, long intervalInMilis, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		this.talon = talon;
		if (intervalInMilis <= 0)
			intervalInMilis = 1;
		this.intervalInMilis = intervalInMilis;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					pidCycle();
					try {
						Thread.sleep(PIDFUtil.this.intervalInMilis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private void pidCycle() {
		if (talon != null)
			position = talon.getSelectedSensorPosition(0);
		error = goal - position;

		output = 0;
		output += f;
		output += p * error;
		output += i * accumulatedError;
		output += d * errorVelocity;
		
		output = (output / (1000.));
		
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
