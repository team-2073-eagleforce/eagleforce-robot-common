package com.team2073.common.motionprofiling;

import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.motionprofiling.lib.trajectory.Trajectory;
import com.team2073.common.motionprofiling.lib.trajectory.TrajectoryGenerator;

import java.util.concurrent.Callable;

import static com.team2073.common.controlloop.PidfControlLoop.PositionSupplier;

public class SCurveProfileManager {

	private MotionProfileControlloop controller;
	private ProfileConfiguration configuration;
	private Trajectory traj;
	private PositionSupplier currentPosition;
	private Callable<Double> betweenProfiles;
	private PidfControlLoop holdingPID;
	private double setpoint;
	private double lastSetpoint;
	private double output;
	private ProfileTrajectoryPoint currPoint;
	private int currentSeg = -1;

	/**
	 * @param currentPosition Provides the current position of the mechanism. Ensure units match that of the profile
	 *                        and of the controller.
	 */
	public SCurveProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, PositionSupplier currentPosition) {
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
	public SCurveProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, PositionSupplier currentPosition, Callable<Double> betweenProfiles) {
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
	public SCurveProfileManager(MotionProfileControlloop controller, ProfileConfiguration configuration, PositionSupplier currentPosition, PidfControlLoop holdingPID) {
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
	public Trajectory getProfile() {
		return traj;
	}

	/**
	 * This method can be called continuously and will construct a profile to the position
	 * specified when a setpoint changes.
	 *
	 * @param setpoint
	 */
	public void setPoint(double setpoint, double currentVelocity) {
		this.setpoint = setpoint;
		if (traj == null || (lastSetpoint != setpoint)) {
			lastSetpoint = setpoint;
			currentSeg = -1;
			if (holdingPID != null)
				holdingPID.resetAccumulatedError();
			if (setpoint < currentPosition.currentPosition()) {
				traj = TrajectoryGenerator.generate(configuration, TrajectoryGenerator.AutomaticStrategy,
						-1 * currentVelocity, 0,
						Math.abs(setpoint - currentPosition.currentPosition()), 0, 0);
			} else {
				traj = TrajectoryGenerator.generate(configuration, TrajectoryGenerator.AutomaticStrategy,
						currentVelocity, 0,
						Math.abs(setpoint - currentPosition.currentPosition()), 0, 0);
			}
			if (setpoint < currentPosition.currentPosition())
				traj.scale(-1d);
			traj.relativePosition(currentPosition.currentPosition());

		}
	}

	public void setPoint(double setpoint) {
		setPoint(setpoint, 0);
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
		if (!isCurrentProfileFinished()) {
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
				output = 0;
			}
		}
		return output;
	}

	private ProfileTrajectoryPoint nextPoint() {
		currentSeg++;
		Trajectory.Segment seg = traj.getSegment(currentSeg);
		currPoint = new ProfileTrajectoryPoint(seg.pos, seg.vel, seg.acc, seg.jerk, seg.dt, seg.dt * currentSeg);
		return currPoint;
	}

	public boolean isCurrentProfileFinished() {
		return currentSeg >= traj.getNumSegments() - 1;
	}

	private ProfileTrajectoryPoint getCurrentPoint() {
		return currPoint;
	}

	public double currProfileTotalTime() {
		return configuration.getInterval() * traj.getNumSegments();

	}

}
