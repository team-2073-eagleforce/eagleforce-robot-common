package com.team2073.common.util;

public class Timer {

	private long startTime;
	private long stopTime = 0;
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

	public void stop() {
		this.stopTime = System.currentTimeMillis();
		this.state = TimerState.STOPPED;
	}

	public boolean hasWaited(long timePassedInMilliseconds) {
//		long currTime = System.currentTimeMillis();
//		return startTime + timePassedInMilliseconds < currTime && (state != TimerState.STOPPED);
		return getElapsedTime() > timePassedInMilliseconds;
	}

	public long getElapsedTime() {
		long elapsed;
		if (state == TimerState.STARTED) {
			elapsed = (System.currentTimeMillis() - startTime);
		} else if (state == TimerState.STOPPED){
			elapsed = (stopTime - startTime);
		} else {
			elapsed = 0;
		}
		return elapsed;
	}

	/**
	 * @deprecated Use {@link #stop()} instead.
	 */
	@Deprecated
	public void reset() {
		stop();
	}
}
