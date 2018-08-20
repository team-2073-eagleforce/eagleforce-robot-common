package com.team2073.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

/**
 * @author pbriggs
 */
class TimerTest {

    @Test
    public void testStopWatch() throws InterruptedException {
        Timer timer = new Timer();
        long delay = 3000;
        timer.start();
        Thread.sleep(delay);
        timer.stop();
        assertThat(timer.getElapsedTime()).isCloseTo(delay, withinPercentage(0.2));
    }


}