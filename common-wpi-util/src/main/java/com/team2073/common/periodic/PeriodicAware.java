package com.team2073.common.periodic;

/** @see PeriodicRunner */
public interface PeriodicAware {

	/** @see PeriodicRunner */
	void onPeriodic();

	default void registerSelf(PeriodicRunner periodicRunner) {
		periodicRunner.register(this);
	}

}
