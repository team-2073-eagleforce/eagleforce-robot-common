package com.team2073.common.periodic;

import com.team2073.common.ctx.RobotContext;

/**
 * See {@link PeriodicRunner}
 *
 * @author Preston Briggs
 */
public interface PeriodicRunnable {

	/** See {@link PeriodicRunner} */
	void onPeriodic();

	/**
	 * Automatically registers this instance with the {@link PeriodicRunner} provided by the {@link RobotContext}.
	 * <br/>
	 * <br/>
	 * Generally only meant to be called internally from implementations of this interface from their contructor.
	 * <br/>
	 * <br/>
	 * Ex:
	 * <pre>
	 *
	 * public class Foo implements PeriodicRunnable {
	 *
	 * 	public Foo() {
	 * 	   registerWithPeriodicRunner();
	 * 	}
	 *
	 * 	{@literal @}Override
	 * 	public void onPeriodic() {
	 * 	   // do something
	 * 	}
	 * }
	 * </pre>
	 */
	default void registerWithPeriodicRunner() {
		RobotContext.getInstance().getPeriodicRunner().register(this);
	}

	/** See {@link #registerWithPeriodicRunner()} */
	default void registerWithPeriodicRunner(String name) {
		RobotContext.getInstance().getPeriodicRunner().register(this, name);
	}

}
