package com.team2073.common.util;

public class TimerUtil {

	private long startTime;
	private TimerState state = TimerState.UNINITIALIZED;

	enum TimerState {
		STOPPED, STARTED, UNINITIALIZED;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		state = TimerState.STARTED;
	}

	public void start(long startingTime) {
		startTime = startingTime;
		state = TimerState.STARTED;
	}

	public boolean hasWaited(long timePassedInMilliseconds) {
		long currTime = System.currentTimeMillis();
		return startTime + timePassedInMilliseconds < currTime && (state != TimerState.STOPPED);
	}

	public void reset() {
		state = TimerState.STOPPED;
		startTime = -1;
	}
}
