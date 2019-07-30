package com.team2073.common.pathfollowing;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.motionprofiling.lib.trajectory.Path;
import com.team2073.common.motionprofiling.lib.trajectory.Trajectory.Segment;
import com.team2073.common.pathfollowing.math.RigidTransform2d;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import com.team2073.common.util.ConversionUtil;
import com.team2073.common.util.Timer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PurePursuitManager implements AsyncPeriodicRunnable {

	public enum EndStrategy {
		END_ROUTINE,
		AUTO_LOAD_NEXT_PATH,
		WAIT_BEFORE_NEXT_PATH,
		START_NEXT_ON_COMMAND;
	}

	private PurePursuitDrive ppd;
	private CurvatureController controller;
	private Runnable afterRoutine;
	private ArrayList<AutoRoutine> routines;
	private boolean enabled = true;
	private double leftOutput;
	private double rightOutput;
	private AutoRoutine currentRoutine;
	private Callable<RigidTransform2d> robotPose;
	private Timer timer = new Timer();
	private int index;
	private boolean routineFinished;

	public PurePursuitManager(PurePursuitDrive ppd, CurvatureController controller, Runnable afterRoutine, ArrayList<AutoRoutine> routines, Callable<RigidTransform2d> robotPose) {
		this.ppd = ppd;
		this.controller = controller;
		this.afterRoutine = afterRoutine;
		this.routines = routines;
		this.robotPose = robotPose;
		RobotContext.getInstance().getPeriodicRunner().registerAsync(this, "PurePursuitManager", 10);
		controller.setCurvature(this::calcCurvature);
		controller.setClosestPointIndex(() -> ppd.findClosestPointIndex());
	}

	private double calcCurvature() throws Exception {
		RigidTransform2d robotPosition = robotPose.call();
		Point2D robotPos = PurePursuitDrive.rigidTransformToPoint2D(robotPosition);
		Segment closestPoint = ppd.closesetPoint(robotPos);
		double lookahead = ppd.calcLookAheadDist(closestPoint.vel);
		Point2D lookaheadPoint = PurePursuitDrive.segmentToPoint2D(ppd.lookaheadPoint(lookahead, robotPos));
		return ppd.curvature(robotPosition, lookahead, lookaheadPoint);
	}

	public boolean isRoutineFinished() {
		return routineFinished;
	}

	@Override
	public void onPeriodicAsync() {
		if (!enabled)
			return;

		if (routines.isEmpty()) {
			routineFinished = true;
			afterRoutine.run();
			return;
		}

		if (currentRoutine == null) {
			currentRoutine = routines.get(0);
			controller.setForward(currentRoutine.getDirection() >= 1);
			ppd.setPath(currentRoutine.getPath());
			controller.setPath(currentRoutine.getPath());
		}

		if (!isCurrentPathFinished()) {
			controller.update();
			leftOutput = controller.getLeftOutput();
			rightOutput = controller.getRightOutput();
			return;
		}

		switch (currentRoutine.getEndStrategy()) {
			case END_ROUTINE:
				routines = new ArrayList<>();
				afterRoutine.run();
				break;
			case WAIT_BEFORE_NEXT_PATH:
				if (timer.getState() != Timer.TimerState.STARTED || timer.getState() == Timer.TimerState.UNINITIALIZED) {
					timer.start();
				}
				if (timer.hasWaited(ConversionUtil.secondsToMs(currentRoutine.getDelayInSeconds()))) {
					loadNextPath();
					timer.stop();
				}
				break;
			case AUTO_LOAD_NEXT_PATH:
				loadNextPath();
				break;
			case START_NEXT_ON_COMMAND:
				try {
					if (currentRoutine.getShouldContinue().call()) {
						loadNextPath();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}

	private void loadNextPath() {
		routines.remove(currentRoutine);
		currentRoutine = routines.get(0);
		controller.setForward(currentRoutine.getDirection() > 0);
		ppd.setPath(currentRoutine.getPath());
		controller.setPath(currentRoutine.getPath());
		index = 0;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getLeftOutput() {
		return leftOutput;
	}

	public double getRightOutput() {
		return rightOutput;
	}


	public Path getCurrentPath() {
		return null;
	}

	private boolean isCurrentPathFinished() {
		boolean complete = currentRoutine.getPath().getRobotTrajectory().getNumSegments() - 1 == index;
		index++;
		return complete;
	}


}
