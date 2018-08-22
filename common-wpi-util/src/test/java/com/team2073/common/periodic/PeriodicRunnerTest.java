package com.team2073.common.periodic;

import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                    assertEquals(simplePeriodicAware.periodicIterations, env.getCurrRobotPeriodic(),
                            "Expected cycle iterations did not match actual.");
                });
    }

    private static class SimplePeriodicAware implements PeriodicAware {

        private Logger log = LoggerFactory.getLogger(getClass());

        int periodicIterations = 0;

        @Override
        public void onPeriodic() {
            periodicIterations++;
            // TODO: create a test where we take too long on the last iteration and then set some flag after complete.
            // This flage should never actually get set since the thread will get killed when it takes too long.

//            if (periodicIterations == 90) {
//                log.debug("Last iteration, sleeping...");
//                ThreadUtil.sleep(6000);
//                log.debug("Last iteration, sleeping complete.");
//            }
        }
    }
}