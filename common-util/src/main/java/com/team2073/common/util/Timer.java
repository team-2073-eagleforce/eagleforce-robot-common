package com.team2073.common.util;

public class Timer {

	private long startTime;
	private long pauseTime = 0;
	private TimerState state = TimerState.UNINITIALIZED;

	public TimerState getState() {
		return state;
	}

	public enum TimerState {
		STOPPED, STARTED, UNINITIALIZED, PAUSED;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		state = TimerState.STARTED;
	}

	public void start(long startingTime) {
		startTime = startingTime;
		state = TimerState.STARTED;
	}

	public void pause() {
		this.pauseTime = System.currentTimeMillis();
		this.state = TimerState.PAUSED;
	}
	public void stop(){
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
		} else if (state == TimerState.PAUSED) {
			elapsed = (pauseTime - startTime);
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
