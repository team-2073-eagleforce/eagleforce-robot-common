package com.team2073.common.periodic;

import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.util.ThreadUtil;
import com.team2073.common.util.Timer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

/**
 * @author pbriggs
 */
class PeriodicRunnerTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void basicPeriodicAwareTest() {
        SimplePeriodicAware simplePeriodicAware = new SimplePeriodicAware();
        PeriodicRunner.registerInstance(simplePeriodicAware);

        SimulationEnvironmentRunner.create()
                .withPeriodicComponent(PeriodicRunner.getInstance())
                .run(env -> {
                    assertThat(env.getCurrRobotPeriodic())
                            .isEqualTo(simplePeriodicAware.periodicIterations);
                });
    }

    @Test
    public void verifyDurationMathTest() {
        PeriodicRunner periodicRunner = PeriodicRunner.getInstance();
        DurationRecordingPeriodicAware recorder1 = new DurationRecordingPeriodicAware(1);
        DurationRecordingPeriodicAware recorder2 = new DurationRecordingPeriodicAware(7);
        DurationRecordingPeriodicAware recorder3 = new DurationRecordingPeriodicAware(7);
        PeriodicRunner.registerInstance(recorder1);
        PeriodicRunner.registerInstance(recorder2);
        PeriodicRunner.registerInstance(recorder3);

        SimulationEnvironmentRunner.create()
                .withPeriodicComponent(periodicRunner)
                .run(env -> {
                    long intanceTotal = periodicRunner.getInstanceLoopHistory().getTotal();
                    long fullLoopTotal = periodicRunner.getFullLoopHistory().getTotal();
                    int recorder1Total = recorder1.totalDelay();
                    int recorder2Total = recorder2.totalDelay();
                    int recorder3Total = recorder3.totalDelay();
                    int allRecorderTotal = recorder1Total + recorder2Total + recorder3Total;
                    assertThat(intanceTotal).isGreaterThanOrEqualTo(recorder1Total);
                    assertThat(intanceTotal).isGreaterThanOrEqualTo(recorder2Total);
                    assertThat(intanceTotal).isGreaterThanOrEqualTo(recorder3Total);
                    assertThat(intanceTotal).isCloseTo(allRecorderTotal, withinPercentage(1L));
                });
    }

    private static class SimplePeriodicAware implements PeriodicAware {
        int periodicIterations = 0;

        @Override
        public void onPeriodic() {
            periodicIterations++;
            // TODO: create a test where we take too long on the last iteration and then set some flag after complete.
            // This flag should never actually get set since the thread will get killed when it takes too long.

            ThreadUtil.sleep(1);
            if (periodicIterations == 90) {
                ThreadUtil.sleep(40);
            }
        }
    }

    private static class DurationRecordingPeriodicAware implements PeriodicAware {

        private Logger log = LoggerFactory.getLogger(getClass());

        private int periodicIterations = 0;
        private int delayMillis;
        private int totalElapsed;
        private Timer timer = new Timer();

        public DurationRecordingPeriodicAware(int delayMillis) {
            this.delayMillis = delayMillis;
        }

        @Override
        public void onPeriodic() {
            timer.start();
            periodicIterations++;
            ThreadUtil.sleep(delayMillis);
            totalElapsed += timer.getElapsedTime();
        }

        public int getPeriodicIterations() {
            return periodicIterations;
        }

        public int totalDelay() {
            return totalElapsed;
//            return delayMillis * periodicIterations;
        }
    }
}