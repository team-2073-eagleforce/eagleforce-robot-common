package com.team2073.common.pathfollowing;

import com.team2073.common.motionprofiling.lib.trajectory.Path;

import java.util.concurrent.Callable;

public class CurvatureController {
	private final Callable<Double> curvature;
	private final Callable<Double> leftVelocity;
	private final Callable<Double> rightVelocity;
	private final Callable<Integer> closestPointIndex;
	private final double trackWidth;
	private final double kv;
	private final double ka;
	private final double kp;
	private Path path;
	private double leftOutput;
	private double rightOutput;
	private boolean isForward = true;

	/**
	 * Callable based constructor, more will be handled automatically by this constructor.
	 *
	 * @param signedCurvature   Positive is a right turn, Negative for left turn.
	 * @param pathToFollow      can be optionally provided later.
	 * @param leftVelocity      should return the linear velocity of the left side, in ft/s
	 * @param rightVelocity     should return the linear velocity of the right side, in ft/s
	 * @param trackWidth        width between each drive side, in ft (recommended to be a few inches larger to compensate
	 *                          for turning scrub)
	 * @param closestPointIndex the index of the closest point to the robot on the path.
	 * @param kv                feed forward velocity constant, usually approx 1/MAX_VELOCITY
	 * @param ka                feed forward acceleration constant, applies additional power while accelerating
	 * @param kp                feedback proportional constant, helps "stick" to the path better but can cause oscillation
	 */
	public CurvatureController(Path pathToFollow, Callable<Double> signedCurvature, Callable<Double> leftVelocity,
	                           Callable<Double> rightVelocity, Callable<Integer> closestPointIndex, double trackWidth,
	                           double kv, double ka, double kp) {
		this.curvature = signedCurvature;
		this.path = pathToFollow;
		this.leftVelocity = leftVelocity;
		this.rightVelocity = rightVelocity;
		this.trackWidth = trackWidth;
		this.closestPointIndex = closestPointIndex;
		this.kv = kv;
		this.ka = ka;
		this.kp = kp;

	}

	/**
	 * Parameter based constructor, used when wanting to have more manual control.
	 *
	 * @param pathToFollow
	 * @param trackWidth   width between each drive side, in ft (recommended to be a few inches larger to compensate
	 *                     for turning scrub)
	 * @param kv           feed forward velocity constant, usually approx 1/MAX_VELOCITY
	 * @param ka           feed forward acceleration constant, applies additional power while accelerating
	 * @param kp           feedback proportional constant, helps "stick" to the path better but can cause oscillation
	 */
	public CurvatureController(Path pathToFollow, double trackWidth, double kv, double ka, double kp) {
		this.path = pathToFollow;
		this.trackWidth = trackWidth;
		this.kv = kv;
		this.ka = ka;
		this.kp = kp;
		this.curvature = null;
		this.rightVelocity = null;
		this.leftVelocity = null;
		this.closestPointIndex = null;
	}

	/**
	 * To be called only if constructor with Callables was used.
	 */
	public void update() {
		try {
			update(curvature.call(), leftVelocity.call(), rightVelocity.call(), closestPointIndex.call());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(double signedCurvature, double leftVelocity, double rightVelocity, int closestPointIndex) {
		double targetVel = (path.getLeftWheelTrajectory().getSegment(closestPointIndex).vel +
				path.getRightWheelTrajectory().getSegment(closestPointIndex).vel) / 2d;
		double targetLeftAcc = path.getLeftWheelTrajectory().getSegment(closestPointIndex).acc;
		double targetRightAcc = path.getRightWheelTrajectory().getSegment(closestPointIndex).acc;

		double targetLeftVel = (targetVel * (1 + ((signedCurvature * trackWidth) / 2)));
		double targetRightVel = (targetVel * (1 - ((signedCurvature * trackWidth) / 2)));

		leftOutput = kv * targetLeftVel + ka * targetLeftAcc + kp * (targetLeftVel - leftVelocity);

		rightOutput = kv * targetRightVel + ka * targetRightAcc + kp * (targetRightVel - rightVelocity);

		if (!isForward) {
			leftOutput *= -1;
			rightOutput *= -1;
		}
	}

	public double getLeftOutput() {
		return leftOutput;
	}

	public double getRightOutput() {
		return rightOutput;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setForward(boolean forward) {
		isForward = forward;
	}
}
