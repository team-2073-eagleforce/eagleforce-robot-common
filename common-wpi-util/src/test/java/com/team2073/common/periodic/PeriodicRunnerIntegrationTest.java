package com.team2073.common.periodic;

import com.team2073.common.CommonConstants.TestTags;
import com.team2073.common.periodic.PeriodicRunner.InstanceAwareDurationHistory;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.team2073.common.periodic.PeriodicRunnerIntegrationTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

/**
 * @author pbriggs
 */
@Tag(TestTags.INTEGRATION_TEST)
class PeriodicRunnerIntegrationTest {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    public void periodicRunner_WHEN_PeriodicAwareThrowsException_SHOULD_ContinueRunning() {
        // TODO
    }

    /**
     * Register a basic {@link PeriodicAware} and verify it was called as many times as the {@link PeriodicRunner} says it looped.
     */
    @Test
    public void periodicRunner_WHEN_PeriodicAwareRegistered_SHOULD_InvokeRegisteredPeriodicAware() {
        PeriodicRunner runner = new PeriodicRunner();
        IterationAwarePeriodicAwareImpl periodic1 = new IterationAwarePeriodicAwareImpl();
        runner.register(periodic1);

        SimulationEnvironmentRunner.create()
                .withPeriodicComponent(runner)
                .run(env -> {
                    assertEnvironmentRan(env);
                    assertPeriodicAwareInstanceCalledAtLeastOnce(periodic1);
                    assertNonAsyncPeriodicAwareInstanceCalledCorrectNumberOfTimes(env, periodic1);
                });
    }

    @Test
    public void periodicRunner_WHEN_PeriodicAwareRegistered_SHOULD_CalculateMathCorrectly() {
        PeriodicRunner runner = new PeriodicRunner();
        DurationRecordingPeriodicAware periodic1 = new DurationRecordingPeriodicAware(1);
        DurationRecordingPeriodicAware periodic2 = new DurationRecordingPeriodicAware(7);
        DurationRecordingPeriodicAware periodic3 = new DurationRecordingPeriodicAware(7);
        runner.register(periodic1);
        runner.register(periodic2);
        runner.register(periodic3);

        SimulationEnvironmentRunner.create()
                .withPeriodicComponent(runner)
                .run(env -> {
                    String errMsg;
                    assertEnvironmentRan(env);
                    assertPeriodicRunnerRan(runner);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic1);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic2);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic3);

                    errMsg = "Miscalculation in average math in " + PeriodicRunner.class.getSimpleName();
                    InstanceAwareDurationHistory loopHistory = runner.getInstanceLoopHistory();
                    long instanceTotal = loopHistory.getTotal();
                    double instanceAvg = loopHistory.getAverage();
                    long instanceCount = loopHistory.getCount();
                    double expectedAvg = (double) instanceTotal / instanceCount;
                    assertThat(loopHistory.getAverage()).as(errMsg).isEqualTo(expectedAvg);

                    errMsg = "DurationRecordingPeriodicAware durations do not correlate to actual readings.";
                    long recorder1Total = periodic1.totalDelay();
                    long recorder2Total = periodic2.totalDelay();
                    long recorder3Total = periodic3.totalDelay();
                    long allRecorderTotal = recorder1Total + recorder2Total + recorder3Total;
                    assertThat(instanceTotal).as(errMsg).isGreaterThanOrEqualTo(recorder1Total);
                    assertThat(instanceTotal).as(errMsg).isGreaterThanOrEqualTo(recorder2Total);
                    assertThat(instanceTotal).as(errMsg).isGreaterThanOrEqualTo(recorder3Total);
                    assertThat(instanceTotal).as(errMsg).isCloseTo(allRecorderTotal, withinPercentage(1L));
                });
    }

    @Test
    public void periodicRunner_WHEN_PeriodicAwareRegisteredAsAsync_SHOULD_RunInSeparateThreadAtSpecifiedDuration() {
        PeriodicRunner runner = new PeriodicRunner();
        TotalDurationAwarePeriodicAware periodic1 = new TotalDurationAwarePeriodicAware(5);
        TotalDurationAwarePeriodicAware periodic2 = new TotalDurationAwarePeriodicAware(10);
        TotalDurationAwarePeriodicAware periodic3 = new TotalDurationAwarePeriodicAware(200);
        runner.registerAsync(periodic1, periodic1.period);
        runner.registerAsync(periodic2, periodic2.period);
        runner.registerAsync(periodic3,  periodic3.period);

        runner.register(new IterationAwarePeriodicAwareImpl(), "Just here to make sure the non-async loop has something to loop over :) ");

        SimulationEnvironmentRunner.create()
                .withPeriodicComponent(runner)
                .withIterationCount(400)
                .run(env -> {
                    String errMsg;

                    assertEnvironmentRan(env);
                    assertPeriodicRunnerRan(runner);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic1);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic2);
                    assertDurationAwareInstanceCalledAtLeastOnce(periodic3);

                    errMsg = "Expected PeriodicAware component to get called about once every [%s] millis but it did " +
                            "not. Verify the async loop is functioning in " + PeriodicRunner.class.getSimpleName() + ".";
                    assertThat(periodic1.avgCycle()).as(errMsg, periodic1.period).isCloseTo(periodic1.period, withinPercentage(1L));
                    assertThat(periodic2.avgCycle()).as(errMsg, periodic2.period).isCloseTo(periodic2.period, withinPercentage(1L));
                    assertThat(periodic3.avgCycle()).as(errMsg, periodic3.period).isCloseTo(periodic3.period, withinPercentage(1L));
                });
    }
}