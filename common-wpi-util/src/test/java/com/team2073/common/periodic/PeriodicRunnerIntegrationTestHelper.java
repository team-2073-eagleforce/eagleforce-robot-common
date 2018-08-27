package com.team2073.common.periodic;

import com.team2073.common.simulation.env.SimulationEnvironment;
import com.team2073.common.util.Timer;

import static org.assertj.core.api.Assertions.*;

/**
 * @author pbriggs
 */
public class PeriodicRunnerIntegrationTestHelper {

    public static void assertEnvironmentRan(SimulationEnvironment env) {
        String errMsg = env.getClass().getSimpleName() + " did not run properly. Simulation problem, " +
                "likely not a problem with the code under test.";
        int envIterations = env.getCurrRobotPeriodic();
        assertThat(envIterations).as(errMsg).isGreaterThan(0);
    }

    public static void assertPeriodicRunnerRan(PeriodicRunner runner) {
        String errMsg = runner.getClass().getSimpleName() + " either did not run properly or had no instance to run.";
        long instanceTotal = runner.getInstanceLoopHistory().getTotal();
        long fullLoopTotal = runner.getFullLoopHistory().getTotal();
        assertThat(instanceTotal).as(errMsg).isGreaterThan(0);
        assertThat(fullLoopTotal).as(errMsg).isGreaterThan(0);
    }

    public static void assertPeriodicAwareInstanceCalledAtLeastOnce(IterationAwarePeriodicAware... periodicAware) {
        String errMsg = "[%s] was never called. Check " + PeriodicRunner.class.getSimpleName() + ".";
        for (IterationAwarePeriodicAware periodic : periodicAware) {
            String className = periodic.getClass().getSimpleName();
            int observedIterations = periodic.getTotalIterations();
            assertThat(observedIterations).as(errMsg, className).isGreaterThan(0);
        }
    }

    public static void assertDurationAwareInstanceCalledAtLeastOnce(DurationAwarePeriodicAware... periodicAware) {
        String errMsg = "[%s] was never called. Check " + PeriodicRunner.class.getSimpleName() + ".";
        for (DurationAwarePeriodicAware periodic : periodicAware) {
            String className = periodic.getClass().getSimpleName();
            assertPeriodicAwareInstanceCalledAtLeastOnce((IterationAwarePeriodicAware) periodic);
            long totalDelay = periodic.totalDelay();
            assertThat(totalDelay).as(errMsg, className).isGreaterThan(0);
        }
    }

    public static void assertNonAsyncPeriodicAwareInstanceCalledCorrectNumberOfTimes(SimulationEnvironment env, IterationAwarePeriodicAware... periodicAware) {
        String errMsg = "SimplePeriodicAware iterations do not correlate to actual iterations.";
        int envIterations = env.getCurrRobotPeriodic();
        for (IterationAwarePeriodicAware periodic : periodicAware) {
            int observedIterations = periodic.getTotalIterations();
            assertThat(envIterations).as(errMsg).isEqualTo(observedIterations);
        }
    }


    // ============================================================
    // Classes
    // ============================================================

    interface IterationAwarePeriodicAware extends PeriodicAware {
        int getTotalIterations();
    }

    interface DurationAwarePeriodicAware extends IterationAwarePeriodicAware {
        long totalDelay();
    }

    static class IterationAwarePeriodicAwareImpl implements IterationAwarePeriodicAware {
        private int periodicIterations = 0;

        @Override
        public void onPeriodic() {
            periodicIterations++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getTotalIterations() {
            return periodicIterations;
        }
    }

    static class DurationRecordingPeriodicAware extends IterationAwarePeriodicAwareImpl implements DurationAwarePeriodicAware {

        private int delayMillis;
        private int totalElapsed;
        private Timer timer = new Timer();

        public DurationRecordingPeriodicAware(int delayMillis) {
            this.delayMillis = delayMillis;
        }

        @Override
        public void onPeriodic() {
            timer.start();
            super.onPeriodic();
            totalElapsed += timer.getElapsedTime();
        }

        public long totalDelay() {
            return totalElapsed;
        }
    }

    static class TotalDurationAwarePeriodicAware extends IterationAwarePeriodicAwareImpl implements DurationAwarePeriodicAware {

        final int period;
        long initialPeriodicTimestamp;
        long lastPeriodicTimestamp;

        public TotalDurationAwarePeriodicAware(int period) {
            this.period = period;
        }

        @Override
        public void onPeriodic() {
            super.onPeriodic();
            long curr = System.currentTimeMillis();
            if (initialPeriodicTimestamp == 0) {
                initialPeriodicTimestamp = curr;
            }
            lastPeriodicTimestamp = curr;
        }

        public long totalDelay() {
            return lastPeriodicTimestamp - initialPeriodicTimestamp;
        }

        public double avgCycle() {
            return (double) totalDelay() / (getTotalIterations() - 1);
        }
    }
}
