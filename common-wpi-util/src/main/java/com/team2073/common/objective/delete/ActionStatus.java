package com.team2073.common.objective.delete;

public class ActionStatus<T> {
	private final T action;
	private boolean finished = false;
	private boolean interrupted = false;

	public ActionStatus(T action) {
		this.action = action;
	}

	public T getAction() {
		return action;
	}

	public boolean isFinished() {
		return finished;
	}

	public void finish() {
		this.finished = true;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void interrupt() {
		this.interrupted = true;
	}
}
