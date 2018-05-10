package com.team2073.common.util;

public class TimerUtil {
	
	private long startTime;
	private TimerState state = TimerState.STOPPED;
	
	enum TimerState{
		STOPPED, STARTED;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
		state = TimerState.STARTED;
	}
	
	public  boolean hasWaited(long timePassedInMiliseconds) {
		long currTime = System.currentTimeMillis();
		return startTime + timePassedInMiliseconds < currTime && state != TimerState.STOPPED;
	}
	
	public void reset() {
		state = TimerState.STOPPED;
		startTime = -1;
	}
}
