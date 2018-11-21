package com.team2073.common.periodic;

/**
 * See {@link PeriodicRunner}
 *
 * @author Preston Briggs
 */
public interface PeriodicRunnable {

	/** See {@link PeriodicRunner} */
	void onPeriodic();

	/** @deprecated Use {@code RobotContext.getInstance().getPeriodicRunner().register(...)} instead. */
	@Deprecated
	default void registerSelf(PeriodicRunner periodicRunner) {
		periodicRunner.register(this);
	}

}
