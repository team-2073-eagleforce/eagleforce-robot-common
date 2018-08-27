package com.team2073.common.objective;

public class StatusChecker {
	
	private boolean complete = false;
	private boolean interrupted = false;

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		this.complete = true;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void interrupt() {
		this.interrupted = true;
	}

}
