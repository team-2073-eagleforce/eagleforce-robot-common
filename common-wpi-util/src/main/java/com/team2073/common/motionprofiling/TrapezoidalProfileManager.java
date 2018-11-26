package com.team2073.common.motionprofiling;

import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.controlloop.PidfControlLoop;

import java.util.concurrent.Callable;

public class TrapezoidalProfileManager {

	private MotionProfileControlloop controller;
	private ProfileConfiguration configuration;
	private TrapezoidalVelocityProfile profile;
	private Callable<Double> currentPosition;
	private Callable<Double> betweenProfiles;
	private PidfControlLoop holdingPID;
	private double setpoint;
	private double lastSetpoint;
	private double output;

	/**
	 * @param currentPosition Provides the current position of the mechanism. Ensure units match that of the profile
	 *                        and of the controller.
	 */
	public TrapezoidalProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, Callable<Double> currentPosition) {
		this.controller = controller;
		this.configuration = configuration;
		this.currentPosition = currentPosition;
		controller.dataPointCallable(this::nextPoint);
		controller.updatePosition(currentPosition);
	}

	/**
	 * '
	 *
	 * @param currentPosition Provides the current position of the mechanism. Ensure units match that of the profile
	 *                        and of the controller.
	 * @param betweenProfiles After a profile has executed before the next setpoint is given, usually a method for
	 *                        holding the mechanism in place. The callable should return a percent or voltage output for the motor.
	 */
	public TrapezoidalProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, Callable<Double> currentPosition, Callable<Double> betweenProfiles) {
		this.controller = controller;
		this.configuration = configuration;
		this.currentPosition = currentPosition;
		controller.dataPointCallable(this::nextPoint);
		controller.updatePosition(currentPosition);
		this.betweenProfiles = betweenProfiles;
	}

	/**
	 * '
	 *
	 * @param currentPosition Provides the current position of the mechanism. Ensure units match that of the profile
	 *                        and of the controller.
	 * @param holdingPID      A manged pid controller for holding positions after finishing a profile. Otherwise provide a callable that returns a percent output or voltage.
	 */
	public TrapezoidalProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, Callable<Double> currentPosition, PidfControlLoop holdingPID) {
		this.controller = controller;
		this.configuration = configuration;
		this.currentPosition = currentPosition;
		controller.dataPointCallable(this::nextPoint);
		controller.updatePosition(currentPosition);
		holdingPID.setPositionSupplier(currentPosition);
		this.holdingPID = holdingPID;
	}

	/**
	 * If no setpoint has been given yet, profile will be null;
	 *
	 * @return
	 */
	public TrapezoidalVelocityProfile getProfile() {
		return profile;
	}


	/**
	 * This method can be called continuously and will construct a profile to the position
	 * specified when a setpoint changes.
	 *
	 * @param setpoint
	 */
	public void setPoint(double setpoint) {
		this.setpoint = setpoint;
		if (profile == null || (profile.isFinished() && lastSetpoint != setpoint)) {
			try {
				lastSetpoint = setpoint;
				if (holdingPID != null)
					holdingPID.resetAccumulatedError();
				profile = new TrapezoidalVelocityProfile(currentPosition.call(), setpoint, configuration);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public double getOutput() {
		return output;
	}


	/**
	 * Call this method once periodically on the interval specified at instantiation.
	 *
	 * @return
	 */
	public double newOutput() {
		controller.update(configuration.getInterval());
		if (!profile.isFinished()) {
			output = controller.getOutput();
		} else {
			if (holdingPID != null) {
				holdingPID.updateSetPoint(setpoint);
				try {
					holdingPID.updatePID(configuration.getInterval());
					output = holdingPID.getOutput();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (betweenProfiles != null) {
				try {
					output = betweenProfiles.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				output = controller.getOutput();
			}
		}
		return output;
	}


	private ProfileTrajectoryPoint nextPoint() {
		return profile.calculateNextPoint();
	}

}
