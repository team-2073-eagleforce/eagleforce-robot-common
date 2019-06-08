package com.team2073.common.pathfollowing;

import com.team2073.common.motionprofiling.lib.trajectory.Path;

import java.util.concurrent.Callable;

public class AutoRoutine {
	private Path path;
	private double direction;
	private PurePursuitManager.EndStrategy endStrategy;
	private Callable<Boolean> shouldContinue;
	private double delayInSeconds;

	/**
	 * @param path        path to follow
	 * @param direction   1 or -1, negative for backwards
	 * @param endStrategy what to do after path is finished.
	 */
	public AutoRoutine(Path path, double direction, PurePursuitManager.EndStrategy endStrategy) {
		this.path = path;
		this.direction = direction;
		this.endStrategy = endStrategy;
	}

	/**
	 * @param shouldContinue when returns true, the next path will be loaded.
	 */
	public AutoRoutine(Path path, double direction, PurePursuitManager.EndStrategy endStrategy, Callable<Boolean> shouldContinue) {
		this.path = path;
		this.direction = direction;
		this.endStrategy = endStrategy;
		this.shouldContinue = shouldContinue;
	}

	/**
	 * @param delayInSeconds time to wait after given path is finished before next path is loaded.
	 */
	public AutoRoutine(Path path, double direction, PurePursuitManager.EndStrategy endStrategy, double delayInSeconds) {
		this.path = path;
		this.direction = direction;
		this.endStrategy = endStrategy;
		this.delayInSeconds = delayInSeconds;
	}

	public Path getPath() {
		return path;
	}

	public double getDirection() {
		return direction;
	}

	public PurePursuitManager.EndStrategy getEndStrategy() {
		return endStrategy;
	}

	public Callable<Boolean> getShouldContinue() {
		return shouldContinue;
	}

	public double getDelayInSeconds() {
		return delayInSeconds;
	}
}
