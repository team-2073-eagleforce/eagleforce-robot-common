package com.team2073.common.simulation.env;

import com.team2073.common.assertion.Assert;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.simulation.util.function.ExitSimulationDecider;
import com.team2073.common.simulation.util.function.OnSimulationCompleteHandler;
import com.team2073.common.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * @author pbriggs
 */
public class SimulationEnvironmentRunner {

    public static SimulationEnvironmentRunner create() {
        return new SimulationEnvironmentRunner();
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private final SimulationEnvironment simEnv = new SimulationEnvironment();
    private boolean ran;

    // Cycle members
    private final ScheduledExecutorService cycleThreadRunner = Executors.newSingleThreadScheduledExecutor();
    private final SimulationCycleEnvironment cycleEnv = new SimulationCycleEnvironment();
    private final EnvironmentCycleTask cycleTask = new EnvironmentCycleTask(simEnv);
    private final List<SimulationCycleComponent> cycleComponentList = new ArrayList<>();

    // Periodic members
    private final ExecutorService periodicThreadRunner = Executors.newSingleThreadExecutor();
    private final List<PeriodicAware> periodicList = new ArrayList<>();
    private final RobotPeriodicTask periodicTask = new RobotPeriodicTask();
    private ScheduledFuture<?> periodicTaskResult;

    // Configurable members
    private SimulationRobotRunner robotRunner = new SimulationRobotRunner();
    private ExitSimulationDecider exitDecider;
    private int iterationCount = 90;

    // Main run method
    public void run(OnSimulationCompleteHandler onComplete) {

        if (ran)
            throw new IllegalStateException("Cannot run simulation runner more than once.");
        ran = true;

        // Initialization
        log.debug("Initializing simulation environment...");
        exitDecider = new RobotPeriodicIterationCountExitDecider(iterationCount);
        cycleComponentList.forEach(e -> cycleEnv.registerCycleComponent(e));
        periodicList.add(robotRunner);
        log.debug("Initializing simulation environment finished.");

        // Start threads
        log.debug("Invoking simulation environment...");
        periodicTaskResult = cycleThreadRunner.scheduleAtFixedRate(cycleTask, 0, 1, TimeUnit.MILLISECONDS);
        log.debug("Invoking simulation environment finished. Simulation environment running.");

        // Wait for simulation to finish
        while(!simEnv.isExitRequested()) {
            log.trace("Exit not yet requested. Waiting...");
            ThreadUtil.sleep(5);
        }

        // Kill simulation threads
        log.debug("Exiting simulation environment...");
        kill();
        log.debug("Exiting simulation environment finished. Cycle iterations: [{}]. Periodic iterations: [{}].",
                simEnv.getCurrCycle(), simEnv.getCurrRobotPeriodic());

        // Provide an opportunity to run assertions
        log.debug("Calling onComplete callback...");
        onComplete.onComplete(simEnv);
        log.debug("Calling onComplete callback finished.");
    }

    // Public configuration methods
    // TODO: Extract to a configuration object?
    public SimulationEnvironmentRunner withCycleComponent(SimulationCycleComponent... cycleComponent) {
        for (SimulationCycleComponent component : cycleComponent) {
            cycleComponentList.add(component);
        }
        return this;
    }

    public SimulationEnvironmentRunner withRobotRunner(SimulationRobotRunner robotRunner) {
        this.robotRunner = robotRunner;
        return this;
    }

    public SimulationEnvironmentRunner withPeriodicComponent(PeriodicAware... periodicAware) {
        Assert.assertNotNull(periodicAware, "periodicAware");
        for (PeriodicAware periodic : periodicAware) {
            Assert.assertNotNull(periodic, "periodic");
            periodicList.add(periodic);
        }
        return this;
    }

    public SimulationEnvironmentRunner withIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    // Private methods
    private void kill() {

        // Kill periodic thread
        final int timeout = 5;
        final TimeUnit timoutUnit = TimeUnit.SECONDS;
        log.debug("Killing periodic thread...");
        periodicThreadRunner.shutdown();
        boolean finishedSuccessfully = ThreadUtil.awaitTermination(periodicThreadRunner, timeout, timoutUnit);
        if (!finishedSuccessfully) {
            log.warn("Could not kill periodic thread. Timeout of [{} {}] expired instead.", timeout, timoutUnit);
        } else {
            log.debug("Killing periodic thread finished successfully.");
        }

        // Kill cycle thread
        log.debug("Killing cycle thread...");
        // TODO: What happens if this take a little bit to cancel?
        periodicTaskResult.cancel(false);
        log.debug("Killing cycle thread finished.");
    }

    private class EnvironmentCycleTask extends TimerTask {

        private final SimulationEnvironment simEnv;

        public EnvironmentCycleTask(SimulationEnvironment simEnv) {
            this.simEnv = simEnv;
        }

        private Future<?> prevThreadResult;

        private boolean exitPreviouslyRequested = false;

        @Override
        public void run() {

            // Block until run() method has a chance to call kill()
            if (simEnv.isExitRequested()) {
                return;
            }

            int currCycle = simEnv.getCurrCycle();
            log.trace("Running cycle iteration [{}].", currCycle);

            // Run robot periodic every x iterations
            if (currCycle % simEnv.getRobotInterval() == 0) {
                logPrevPeriodicThreadIncomplete();
                prevThreadResult = periodicThreadRunner.submit(periodicTask);
            }

            // Check if it is time to exit
            if (exitDecider.shouldExitSimulation(simEnv)) {
                if (!exitPreviouslyRequested) {
                    log.debug("ExitSimulationDecider initiated exit. Requesting exit.");
                    simEnv.requestExit();
                }
                exitPreviouslyRequested = true;
                return;
            }

            // It is not time to exit yet, run a cycle
            cycleEnv.cycle(simEnv);
            log.trace("Running cycle iteration [{}] finished.", currCycle);
            simEnv.incrementAndGetCycle();
        }

        private void logPrevPeriodicThreadIncomplete() {
            if (prevThreadResult != null && !prevThreadResult.isDone()) {
                // TODO: list the number of threads currently waiting execution
                log.warn("Previous robot thread has not completed and another is being queued to process already. " +
                        "Check for something consuming too much time in one cycle. Current cycle/periodic [{}/{}].",
                        simEnv.getCurrCycle(), simEnv.getCurrRobotPeriodic());
            }
        }
    }

    private class RobotPeriodicTask implements Runnable {

        @Override
        public void run() {
            int currRobotPeriodic = simEnv.incrementAndGetPeriodic();
            log.trace("Running periodic iteration [{}].", currRobotPeriodic);
            periodicList.forEach(e -> e.onPeriodic());
            log.trace("Finished running periodic iteration [{}].", currRobotPeriodic);
        }
    }

    private class RobotPeriodicIterationCountExitDecider implements ExitSimulationDecider {

        private int iterationCount;

        public RobotPeriodicIterationCountExitDecider(int iterationCount) {
            this.iterationCount = iterationCount;
        }

        @Override
        public boolean shouldExitSimulation(SimulationEnvironment simEnv) {
            return simEnv.getCurrCycle() >= iterationCount * simEnv.getRobotInterval() - 1;
        }
    }
}
