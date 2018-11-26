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

    /** See {@link PeriodicRunnable#autoRegisterWithPeriodicRunner()} */
    default void autoRegisterWithPeriodicRunner() {
        RobotContext.getInstance().getPeriodicRunner().autoRegisterAsync(this);
    }

    /** See {@link PeriodicRunnable#autoRegisterWithPeriodicRunner()} */
    default void autoRegisterWithPeriodicRunner(String name) {
        RobotContext.getInstance().getPeriodicRunner().autoRegisterAsync(this, name);
    }

    /** See {@link PeriodicRunnable#autoRegisterWithPeriodicRunner()} */
    default void autoRegisterWithPeriodicRunner(int interval) {
        RobotContext.getInstance().getPeriodicRunner().autoRegisterAsync(this, interval);
    }

    /** See {@link PeriodicRunnable#autoRegisterWithPeriodicRunner()} */
    default void autoRegisterWithPeriodicRunner(String name, int interval) {
        RobotContext.getInstance().getPeriodicRunner().autoRegisterAsync(this, name, interval);
    }
}
