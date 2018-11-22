package com.team2073.common.periodic;

import com.team2073.common.ctx.RobotContext;

/**
 * See {@link PeriodicRunner}
 *
 * @author Preston Briggs
 */
public interface AsyncPeriodicRunnable {

    /** See {@link PeriodicRunner} */
    void onPeriodicAsync();

    /** See {@link PeriodicRunnable#registerWithPeriodicRunner()} */
    default void registerWithPeriodicRunner() {
        RobotContext.getInstance().getPeriodicRunner().registerAsync(this);
    }

    /** See {@link PeriodicRunnable#registerWithPeriodicRunner()} */
    default void registerWithPeriodicRunner(String name) {
        RobotContext.getInstance().getPeriodicRunner().registerAsync(this, name);
    }

    /** See {@link PeriodicRunnable#registerWithPeriodicRunner()} */
    default void registerWithPeriodicRunner(int interval) {
        RobotContext.getInstance().getPeriodicRunner().registerAsync(this, interval);
    }

    /** See {@link PeriodicRunnable#registerWithPeriodicRunner()} */
    default void registerWithPeriodicRunner(String name, int interval) {
        RobotContext.getInstance().getPeriodicRunner().registerAsync(this, name, interval);
    }
}
