package com.team2073.common.periodic;

/** @see PeriodicRunner */
public interface PeriodicRunnable {

	/** @see PeriodicRunner */
	void onPeriodic();

	default void registerSelf(PeriodicRunner periodicRunner) {
		periodicRunner.register(this);
	}

}
