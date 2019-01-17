package com.team2073.common.simulation.runner;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.robot.RobotDelegate;
import com.team2073.common.robot.adapter.RobotAdapter;
import com.team2073.common.robot.adapter.RobotAdapterSimulationImpl;
import com.team2073.common.simulation.env.SimulationCycleEnvironment;
import com.team2073.common.simulation.env.SimulationEnvironment;
import com.team2073.common.simulation.function.ExitSimulationDecider;
import com.team2073.common.simulation.function.OnSimulationCompleteHandler;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import com.team2073.common.util.Ex;
import com.team2073.common.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.team2073.common.util.ClassUtil.*;

/**
 * @author Preston Briggs
 */
public class SimulationEnvironmentRunner {

    private static final String CYCLE_THREAD_NAME = "cycle-runner";
    private static final String PERIODIC_THREAD_NAME = "periodic-runner";

    public static SimulationEnvironmentRunner create() {
        return new SimulationEnvironmentRunner();
    }

    private Logger log = LoggerFactory.getLogger(getClass());
    
    private final RobotContext robotContext = RobotContext.getInstance();
    private final SimulationEnvironment simEnv = new SimulationEnvironment();
    private boolean ran;

    // Cycle members
    private final ScheduledExecutorService cycleThreadRunner = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(CYCLE_THREAD_NAME).build());
    private final SimulationCycleEnvironment cycleEnv = new SimulationCycleEnvironment();
    private final EnvironmentCycleTask cycleTask = new EnvironmentCycleTask(simEnv);
    private Throwable cycleException;
    private Throwable periodicException;

    // Periodic members
    private final ExecutorService periodicThreadRunner = Executors
            .newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(PERIODIC_THREAD_NAME).build());
    private List<PeriodicRunnable> periodicList = new ArrayList<>();
    private final RobotPeriodicTask periodicTask = new RobotPeriodicTask();
    private ScheduledFuture<?> periodicTaskResult;
    
    // Configurable members
    private SimulationRobotRunner robotRunner;
    private RobotAdapter robotAdapter;
    private RobotDelegate robotDelegate;
    private ExitSimulationDecider exitDecider;
    private int iterationCount = 90;
    
    // Main run method
    public SimulationEnvironment start() {
        return start(null);
    }

    /** @deprecated Use {@link #start()} instead and use the returned {@link SimulationEnvironment}  */
    @Deprecated
    public SimulationEnvironment start(OnSimulationCompleteHandler onComplete) {

        if (ran)
            throw new IllegalStateException("Cannot run simulation runner more than once.");
        ran = true;
    
        if (!RobotContext.instanceIsSimulationMode())
            throw Ex.illegalState("Must set RobotContext to simulation mode prior to running simulation environment. "
                    + "Use RobotContext.initSimulationInstance().");
    
    
        // Initialization
        log.debug("SimRunner: Initializing simulation environment...");

        log.debug("SimRunner: Iteration count: [{}].", iterationCount);
        exitDecider = new RobotPeriodicIterationCountExitDecider(iterationCount);
    
        if (robotDelegate == null)
            robotDelegate = new NoOpRobotDelegate();
    
        if (robotAdapter == null)
            robotAdapter = new RobotAdapterSimulationImpl(robotDelegate);
    
        if (robotRunner == null)
            robotRunner = new SimulationRobotRunner(robotAdapter);
    
        for (PeriodicRunnable periodicRunnable : periodicList) {
            // We wait until here to add directly to periodic runner in case they call withPeriodicRunner(...)
            // after they have called withPeriodicComponent(...) which would add instances to two diff periodic runners
            robotContext.getPeriodicRunner().register(periodicRunnable);
        }
    
        // Set to null so we fail if we try to use this after here (should periodic runner instead)
        periodicList = null;
    
        log.debug("SimRunner: Initializing simulation environment finished.");
        // End Initialization
        
    
        // Start threads
        log.debug("SimRunner: Starting simulation cycle thread [{}]...", CYCLE_THREAD_NAME);
        periodicTaskResult = cycleThreadRunner.scheduleAtFixedRate(cycleTask, 0, 1, TimeUnit.MILLISECONDS);
        log.debug("SimRunner: Starting simulation cycle thread [{}] finished. Simulation environment running.", CYCLE_THREAD_NAME);

        // Wait for simulation to finish
        while(!simEnv.isExitRequested() && !exceptionOccurred()) {
            log.trace("SimRunner: Exit not yet requested. Waiting...");
            ThreadUtil.sleep(5);
        }

        // Kill simulation threads
        log.debug("SimRunner: Exiting simulation environment...");
        kill();
        log.debug("SimRunner: Exiting simulation environment finished. Cycle iterations: [{}]. Periodic iterations: [{}].",
                simEnv.getCurrCycle(), simEnv.getCurrRobotPeriodic());

        Throwable ex = null;
        if (cycleExceptionOccurred()) {
            ex = cycleException;
        } else if (periodicExceptionOccurred()) {
            ex = periodicException;
        }

        if (ex != null) {
            log.warn("SimRunner: Simulation environment did not finish due to exception [{}] \n\nException:\n\n", ex.getMessage());

            // Sleeps for synchronization of logger vs console output
            ThreadUtil.sleep(5);
            ex.printStackTrace();
            ThreadUtil.sleep(5);
            System.out.println("\n\n\n");
            ThreadUtil.sleep(5);
            log.warn("SimRunner: Not running assertions... Exiting...");

            throw new SimulationInternalException(ex);
        } else {
            if (onComplete != null) {
                // Provide an opportunity to run assertions
                log.debug("SimRunner: Calling onComplete callback...");
                onComplete.onComplete(simEnv);
                log.debug("SimRunner: Calling onComplete callback finished.");
            }
        }
    
        return simEnv;
    }

    // Public configuration methods
    // TODO: Extract to a configuration object?
    public SimulationEnvironmentRunner withCycleComponent(SimulationCycleComponent... cycleComponent) {
        for (SimulationCycleComponent component : cycleComponent) {
            Assert.assertNotNull(component, "component");
            cycleEnv.registerCycleComponent(component);
            log.debug("SimRunner: Registered cycle component [{}].", component.getClass().getSimpleName());
        }
        return this;
    }

    public SimulationEnvironmentRunner withPeriodicRunner(PeriodicRunner periodicRunner) {
        robotContext.setPeriodicRunner(periodicRunner);
        return this;
    }

    public SimulationEnvironmentRunner withPeriodicComponent(PeriodicRunnable... periodicAware) {
        Assert.assertNotNull(periodicAware, "periodicAware");
        for (PeriodicRunnable periodic : periodicAware) {
            Assert.assertNotNull(periodic, "periodic");
            // Don't add directly to periodic runner here in case they call withPeriodicRunner(...) after this method
            periodicList.add(periodic);
        }
        return this;
    }
    
    public SimulationEnvironmentRunner withSimulationRobotRunner(SimulationRobotRunner robotRunner) {
        Assert.assertNotNull(robotRunner, "robotRunner");
        this.robotRunner = robotRunner;
        log.debug("SimRunner: Registered SimulationRobotRunner [{}].", simpleName(robotRunner));
        return this;
    }
    
    public SimulationEnvironmentRunner withRobotAdapter(RobotAdapter robotAdapter) {
        Assert.assertNotNull(robotAdapter, "robotAdapter");
        this.robotAdapter = robotAdapter;
        log.debug("SimRunner: Registered RobotAdapter [{}].", simpleName(robotAdapter));
        return this;
    }
    
    public SimulationEnvironmentRunner withRobotDelegate(RobotDelegate robotDelegate) {
        Assert.assertNotNull(robotDelegate, "robotDelegate");
        this.robotDelegate = robotDelegate;
        log.debug("SimRunner: Registered RobotDelegate [{}].", simpleName(robotDelegate));
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
        log.debug("SimRunner: Killing periodic thread...");
        periodicThreadRunner.shutdown();
        boolean finishedSuccessfully = ThreadUtil.awaitTermination(periodicThreadRunner, timeout, timoutUnit);
        if (!finishedSuccessfully) {
            log.warn("SimRunner: Could not kill periodic thread. Timeout of [{} {}] expired instead.", timeout, timoutUnit);
        } else {
            log.debug("SimRunner: Killing periodic thread finished successfully.");
        }

        // Kill cycle thread
        log.debug("SimRunner: Killing cycle thread...");
        // TODO: What happens if this take a little bit to cancel?
        periodicTaskResult.cancel(false);
        log.debug("SimRunner: Killing cycle thread finished.");
    }

    private void exitOnCycleException(Throwable e) {
        cycleException = e;
    }

    private void exitOnPeriodicException(Throwable e) {
        periodicException = e;
    }

    private boolean exceptionOccurred() {
        return cycleExceptionOccurred() || periodicExceptionOccurred();
    }

    private boolean cycleExceptionOccurred() {
        return cycleException != null;
    }

    private boolean periodicExceptionOccurred() {
        return periodicException != null;
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

            // Skip until run() method has a chance to call kill()
            // TODO: synchronize cycleExceptionOccurred()?
            if (cycleExceptionOccurred()) {
                log.trace("CycleTask: Exception has occurred. Ignoring this cycle invocation...");
                return;
            }
    
            // Skip until run() method has a chance to call kill()
            // TODO: synchronize isExitRequested()?
            if (simEnv.isExitRequested()) {
                log.trace("CycleTask: Exit has been requested. Ignoring this cycle invocation...");
                return;
            }

            int currCycle = simEnv.getCurrCycle();
            log.trace("CycleTask: Running cycle iteration [{}].", currCycle);

            // Run robot periodic every x iterations
            if (currCycle % simEnv.getRobotInterval() == 0) {
                log.trace("CycleTask: Invoking periodic thread...");
                logPrevPeriodicThreadIncomplete();
                prevThreadResult = periodicThreadRunner.submit(periodicTask);
                log.trace("CycleTask: Invoking periodic thread complete.");
            }

            // Check if it is time to exit
            log.trace("CycleTask: Checking if an exit is requested...");
            if (exitDecider.shouldExitSimulation(simEnv)) {
                if (!exitPreviouslyRequested) {
                    log.debug("CycleTask: ExitSimulationDecider initiated exit. Requesting exit.");
                    simEnv.requestExit();
                }
                exitPreviouslyRequested = true;
                return;
            }
            log.trace("CycleTask: Checking if an exit is requested complete. No exit requested.");

            // It is not time to exit yet, run a cycle
            log.trace("CycleTask: Invoking cycle environment...");
            try {
                cycleEnv.cycle(simEnv);
            } catch (Throwable e) {
                log.warn("CycleTask: Exception invoking cycle environment [{}]", e.getMessage(), e);
                exitOnCycleException(e);
                return;
            }
            log.trace("CycleTask: Invoking cycle environment complete.");


            log.trace("CycleTask: Running cycle iteration [{}] finished.", currCycle);
            simEnv.incrementAndGetCycle();
        }

        private void logPrevPeriodicThreadIncomplete() {
            if (prevThreadResult != null && !prevThreadResult.isDone()) {
                // TODO: list the number of threads currently waiting execution
                log.warn("CycleTask: Previous robot thread has not completed and another is being queued to process already. " +
                        "Check for something consuming too much time in one cycle. Current cycle/periodic [{}/{}].",
                        simEnv.getCurrCycle(), simEnv.getCurrRobotPeriodic());
            }
        }
    }

    private class RobotPeriodicTask implements Runnable {

        @Override
        public void run() {
            int currRobotPeriodic = simEnv.incrementAndGetPeriodic();

            if (currRobotPeriodic % 10 == 0 && !log.isTraceEnabled())
                log.debug("PeriodicTask: Running periodic iteration [{}].", currRobotPeriodic);
            else
                log.trace("PeriodicTask: Running periodic iteration [{}].", currRobotPeriodic);
    
            try {
                robotRunner.onPeriodic();
            } catch (Throwable ex) {
                exitOnPeriodicException(ex);
                return;
            }

            log.trace("PeriodicTask: Finished running periodic iteration [{}].", currRobotPeriodic);
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
    
    private static class NoOpRobotDelegate implements RobotDelegate {}
    
}
