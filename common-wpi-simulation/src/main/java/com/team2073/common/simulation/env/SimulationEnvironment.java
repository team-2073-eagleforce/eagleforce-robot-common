package com.team2073.common.simulation.env;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pbriggs
 */
public class SimulationEnvironment {

    // Cycle members
    private int intervalMs = 1;
    private AtomicInteger currCycle = new AtomicInteger(0);

    // Periodic members
    private AtomicInteger currRobotPeriodic = new AtomicInteger(0);

    // Other members
    private AtomicBoolean exitRequested = new AtomicBoolean(false);

    public int incrementAndGetCycle() {
        return currCycle.incrementAndGet();
    }

    public int incrementAndGetPeriodic() {
        return currRobotPeriodic.incrementAndGet();
    }

    public int getIntervalMs() {
        return intervalMs;
    }

    public int getCurrCycle() {
        return currCycle.get();
    }

    public int getCurrRobotPeriodic() {
        return currRobotPeriodic.get();
    }

    public void requestExit() {
        exitRequested.set(true);
    }

    public boolean isExitRequested() {
        return exitRequested.get();
    }
}
