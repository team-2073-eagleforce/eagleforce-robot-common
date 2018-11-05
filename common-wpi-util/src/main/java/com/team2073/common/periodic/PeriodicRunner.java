package com.team2073.common.periodic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.team2073.common.CommonConstants;
import com.team2073.common.assertion.Assert;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRunner;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.Timer;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Manages periodically invoking instances of {@link PeriodicAware}.
 *
 * <h3>Usage</h3>
 * <ol>
 *     <li>
 *         Register instances using either:
 *         <ul>
 *             <li>{@link #register(PeriodicAware, String)}</li>
 *             <li>{@link #registerAsync(PeriodicAware, String, long)}</li>
 *         </ul>
 *     </li>
 *     <li>
 *         Invoke this class by either:
 *         <ul>
 *             <li>Calling {@link PeriodicRunner#onPeriodic()} <b>repeatedly</b> from your Robot's {@link IterativeRobotBase#robotPeriodic()} method</li>
 *             <li>Calling {@link PeriodicRunner#startPeriodicLoop(long)} <b>one time</b> from your Robot's {@link IterativeRobotBase#robotInit()} method</li>
 *         </ul>
 *     </li>
 * </ol>
 *
 * This runner is preferred over normal periodic methods such as {@link Subsystem#periodic()} for the following features:
 *
 * <h3>Exception Handling</h3>
 * Any exceptions thrown by instances will be caught, logged and the loop will continue running. Exceptions thrown during
 * {@link Subsystem#periodic()} or {@link Command#execute()} (the alternative to using this class) are not handled and
 * will cause the robot to stop abruptly requiring a restart.
 *
 * <h3>Elapsed Time Logging</h3>
 * The total elapsed time of a {@link PeriodicAware#onPeriodic()} iteration is captured and instances consuming too much
 * time are logged. Averages and total iteration counts are also recorded and logged. All of this information is available
 * on the SmartDashboard. This only occurs for non-async instances on the main robot thread ({@link #register(PeriodicAware)})
 * since elapsed time of instances in the async thread pool do not matter (a long running instance will not affect other instances).
 *
 * <h3>Asynchronous Registration</h3>
 * Instances can be registered as async which will add them to a separate thread pool so they do not consume time on the
 * main robot thread. This is useful for things that are low priority, are time consuming, or do not need to happen in
 * a specific/deterministic order such as updating the SmartDashboard. The interval (period) at which the instance should
 * be called can be set using {@link #registerAsync(PeriodicAware, long)}.
 *
 * <h3>Circuit Breaker</h3>
 * Instances that throw exceptions will be monitored and a "circuit breaker" will be applied if necessary. Basically we
 * will stop invoking the instance and give it a chance to 'cool down' and self-correct. The duration that a circuit
 * breaker is applied to an instance will increase exponentially allowing short-lived issues to be resolved quickly
 * and limiting excessive log output of long-lived issues.
 *
 * @author pbriggs
 */
public class PeriodicRunner implements SmartDashboardAware, PeriodicAware {

    // TODO: Allow customizing the thread pool size (extract to properties)

    // TODO: Extract to properties
    public static long DEFAULT_ASYNC_PERIOD = 20;
    public static long DEFAULT_SYNC_PERIOD = 20;

	private static final Logger logger = LoggerFactory.getLogger(PeriodicRunner.class);
	private static PeriodicRunner singleton = new PeriodicRunner();

	private final LinkedHashSet<PeriodicInstance> instanceList = new LinkedHashSet<>();
	private final LinkedHashSet<AsyncPeriodicInstance> asyncInstanceList = new LinkedHashSet<>();
	private Timer instanceLoopTimer = new Timer();
	private Timer fullLoopTimer = new Timer();
	private Timer overallTimer = new Timer();
	private boolean started;
	private static NumberFormat formatter = new DecimalFormat("#0.00000");     
	
	/** One 'count' of this loop history represents one full periodic cycle of ALL PeriodicInstances.
	 * This history object exists the life the robot
	 * (it's 'longest' cycle refers to the longest of all periodic cycles). */
	private final InstanceAwareDurationHistory fullLoopHistory = new InstanceAwareDurationHistory();

	/** One 'count' of this loop history represents one cycle of ONE PeriodicInstance.
	 * This history object exists the life the robot
	 * (it's 'longest' cycle refers to the longest of all periodic cycles). */
	private final InstanceAwareDurationHistory instanceLoopHistory = new InstanceAwareDurationHistory();

	/** One 'count' of this loop history represents one cycle of ONE PeriodicInstance.
	 * This history object only exists for one full periodic iteration
	 * (it's 'longest' cycle refers to the longest of the current periodic cycle). */
	private InstanceAwareDurationHistory currInstanceLoopHistory;

	/** @deprecated Use {@link #register(PeriodicAware)} instead. */
	@Deprecated
	public static void registerInstance(PeriodicAware instance) {
		singleton.register(instance);
	}

	/** @deprecated Use {@link #register(PeriodicAware, String)} instead. */
	@Deprecated
	public static void registerInstance(PeriodicAware instance, String name) {
		singleton.register(instance, name);
	}

	public void register(PeriodicAware instance) {
		Assert.assertNotNull(instance, "instance");
		register(instance, instance.getClass().getSimpleName());
	}

	/** Register an instance to be called periodically on the main robot thread. */
	public void register(PeriodicAware instance, String name) {
        Assert.assertNotNull(instance, "instance");
        logger.info("Registering periodic instance: [{}].", name);
        checkStarted(name);
        PeriodicInstance wrapper = new PeriodicInstance(instance, name);
        instanceList.add(wrapper);
        logger.debug("Registering periodic instance: [{}] complete.", name);
    }

    /** @see #registerAsync(PeriodicAware, String, long)  */
    public void registerAsync(PeriodicAware instance) {
        registerAsync(instance, DEFAULT_ASYNC_PERIOD);
    }

    /** @see #registerAsync(PeriodicAware, String, long)  */
    public void registerAsync(PeriodicAware instance, long period) {
        Assert.assertNotNull(instance, "instance");
        registerAsync(instance, instance.getClass().getSimpleName(), period);
    }

    /** @see #registerAsync(PeriodicAware, String, long)  */
    public void registerAsync(PeriodicAware instance, String name) {
        registerAsync(instance, name, DEFAULT_ASYNC_PERIOD);
    }

    /**
     * Register an instance to be called at a specified interval. These instances will be ran on a separate
     * thread pool than the main robot thread.
     *
     * <h3>WARNING!!</h3>
     * The instance is responsible for managing its own thread safety!
     * As a base rule of thumb either do not read AND write to the same variable from multiple threads or if you do,
     * synchronize appropriately. Research Java concurrency synchronization for more info.
     *
     * @param instance The instance to invoke periodically
     * @param period The interval between invocations
     */
	public void registerAsync(PeriodicAware instance, String name, long period) {
		Assert.assertNotNull(instance, "instance");
        logger.info("Registering ASYNC periodic instance: [{}].", name);
		checkStarted(name);
        AsyncPeriodicInstance wrapper = new AsyncPeriodicInstance(instance, name, period);
        asyncInstanceList.add(wrapper);
        logger.debug("Registering ASYNC periodic instance: [{}] complete.", name);
	}

	private void checkStarted(String instanceName) {
		if (started)
			throw new IllegalStateException(String.format("Cannot register instance [%s]. [%s] already started.",
					instanceName, getClass().getSimpleName()));
	}

	/** @deprecated Use {@link #getInstance()#onPeriodic()} instead. */
	@Deprecated
	public static void runPeriodic() {
		getInstance().onPeriodic();
	}
	
	public static double fmt(double number) {
		 return Double.parseDouble(formatter.format(number));
	}

    public void startPeriodicLoop() {
	    startPeriodicLoop(DEFAULT_SYNC_PERIOD);
    }

	public void startPeriodicLoop(long period) {
	    // TODO
        throw new NotYetImplementedException("startPeriodicLoop");
    }

	@Override
	public void onPeriodic() {
		overallTimer.stop();
		logger.trace("Running periodic loop...");

		overallTimer.start();
		onPeriodicInternal();
		overallTimer.stop();

		logger.trace("Running periodic loop complete. Total duration: [{}]", overallTimer.getElapsedTime());
	}

	private void onPeriodicInternal() {
		if (!started) {
            startAsyncThread();
            started = true;
        }

		currInstanceLoopHistory = new InstanceAwareDurationHistory();
		fullLoopTimer.start();

		for (PeriodicInstance wrapper : instanceList) {
			PeriodicAware instance = wrapper.instance;
			instanceLoopTimer.start();
			ExceptionUtil.suppressVoid(instance::onPeriodic, wrapper.getName() + " ::onPeriodic");
			instanceLoopTimer.stop();
			
			long elapsed = instanceLoopTimer.getElapsedTime();
			if(elapsed >= CommonConstants.Diagnostics.LONG_ON_PERIODIC_CALL) {
			    // TODO: COMMON-153: if this gets called x number of times, log it at a higher level
				logger.debug("Long onPeriodic call of [{}] ms from [{}].", elapsed, wrapper.getClass().getSimpleName());
			}

			wrapper.update(elapsed);
			instanceLoopHistory.update(elapsed, wrapper);
			currInstanceLoopHistory.update(elapsed, wrapper);
		}
		fullLoopTimer.stop();
		fullLoopHistory.update(fullLoopTimer.getElapsedTime(), currInstanceLoopHistory.getLongestInstance());

        if (logger.isTraceEnabled()) {
            logger.trace("Periodic loop: Total: [{}] ms. Avg: [{}] ms. Longest: [{}]: ms. From: [{}].",
                    currInstanceLoopHistory.getTotal(), fmt(currInstanceLoopHistory.getAverage()),
                    currInstanceLoopHistory.getLongest(), currInstanceLoopHistory.getLongestInstance().getName());
        }

		if(logger.isTraceEnabled() && currInstanceLoopHistory.getTotal() >= CommonConstants.Diagnostics.LONG_PERIODIC_LOOP) {
			logger.trace("Long periodic loop of [{}] ms. Number of instances looped [{}].",
					currInstanceLoopHistory.getTotal(), instanceList.size());
		}
	}

	private void startAsyncThread() {
	    logger.info("Starting asynchronous thread pool.");
        ThreadFactory threadNameProvider = new ThreadFactoryBuilder().setNameFormat("periodic-%d").build();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10, threadNameProvider);
        for (AsyncPeriodicInstance instance : asyncInstanceList) {
            logger.info("Adding [{}] to ASYNC thread at an interval of [{}] ms.", instance.getName(), instance.getPeriod());
            scheduler.scheduleAtFixedRate(
					new AsyncRunnable(instance), 20, instance.getPeriod(), TimeUnit.MILLISECONDS);
        }
        logger.info("Starting asynchronous thread pool completed.");
    }
	
	public static void registerSmartDashboard(SmartDashboardAwareRunner registry) {
		singleton.registerSmartDashboardInternal(registry);
	}
	
	public void registerSmartDashboardInternal(SmartDashboardAwareRunner registry) {
		registry.registerInstance(this);
	}
	
	@Override
	public void updateSmartDashboard() {
		SmartDashboard.putNumber("periodic.overall.total", fullLoopHistory.getTotal());
		
		SmartDashboard.putNumber("periodic.curr.count", fullLoopHistory.getCount());
		SmartDashboard.putNumber("periodic.curr.total", fullLoopHistory.getTotal());
		SmartDashboard.putNumber("periodic.curr.avg", fullLoopHistory.getAverage());
		if(fullLoopHistory.getLongestInstance() != null) {
			SmartDashboard.putNumber("periodic.curr.longest-instance.longest", fullLoopHistory.getLongest());
			SmartDashboard.putString("periodic.curr.longest-instance.name", fullLoopHistory.getLongestInstance().getName());
			SmartDashboard.putNumber("periodic.curr.longest-instance.avg", fullLoopHistory.getLongestInstance().getAverage());
		}

		SmartDashboard.putNumber("periodic.history.count", instanceLoopHistory.getCount());
		SmartDashboard.putNumber("periodic.history.total", instanceLoopHistory.getTotal());
		SmartDashboard.putNumber("periodic.history.avg", instanceLoopHistory.getAverage());
		if(instanceLoopHistory.getLongestInstance() != null) {
			SmartDashboard.putNumber("periodic.history.longest-instance.longest", instanceLoopHistory.getLongest());
			SmartDashboard.putString("periodic.history.longest-instance.name", instanceLoopHistory.getLongestInstance().getName());
			SmartDashboard.putNumber("periodic.history.longest-instance.avg", instanceLoopHistory.getLongestInstance().getAverage());
		}
	}

	@Override
	public void readSmartDashboard() {
	}

	/** @see #fullLoopHistory */
	public InstanceAwareDurationHistory getFullLoopHistory() {
		return fullLoopHistory;
	}

	/** @see #instanceLoopHistory */
	public InstanceAwareDurationHistory getInstanceLoopHistory() {
		return instanceLoopHistory;
	}

	/** @see #currInstanceLoopHistory */
	public InstanceAwareDurationHistory getCurrInstanceLoopHistory() {
		return currInstanceLoopHistory;
	}

	public static PeriodicRunner getInstance() {
		return singleton;
	}

	// TODO: Change name to PeriodicRegistration to match pattern of RecordableRegistration
	public static class PeriodicInstance {
		private final PeriodicAware instance;
		private final DurationHistory history = new DurationHistory();
		private final String name;

		public PeriodicInstance(PeriodicAware instance, String name) {
			this.instance = instance;
			this.name = name;
		}

		public void update(long time) {
			history.update(time);
		}


		public double getAverage() {
			return history.getAverage();
		}

        public PeriodicAware getInstance() {
            return instance;
        }

        public DurationHistory getHistory() {
            return history;
        }

        public String getName() {
            return name;
        }
    }

    public static class AsyncPeriodicInstance extends PeriodicInstance {

	    private final long period;

        public AsyncPeriodicInstance(PeriodicAware instance, String name, long period) {
            super(instance, name);
            this.period = period;
        }

        public long getPeriod() {
            return period;
        }
    }

	public static class DurationHistory {
		protected long total;
		protected long longest;
		protected long count;
		protected double average;

		public void update(long elapsed) {
			total += elapsed;
			longest = elapsed > longest ? elapsed : longest;
			count++;
			average = (double) total / count;
		}

		public long getTotal() {
			return total;
		}

		public long getLongest() {
			return longest;
		}

		public long getCount() {
			return count;
		}

		public double getAverage() {
			return average;
		}
	}

	public static class InstanceAwareDurationHistory extends DurationHistory {

		protected PeriodicInstance longestInstance;

		public void update(long elapsed, PeriodicInstance instance) {
			Assert.assertNotNull(instance, "instance");
			if (longest <= 0 || elapsed > longest)
				longestInstance = instance;
			update(elapsed);
		}

		public PeriodicInstance getLongestInstance() {
			return longestInstance;
		}
	}

	private static class AsyncRunnable implements Runnable {

		private PeriodicInstance instance;

		public AsyncRunnable(PeriodicInstance instance) {
			this.instance = instance;
		}

		@Override
		public void run() {
			ExceptionUtil.suppressVoid(instance.instance::onPeriodic, "instance::onPeriodic");
		}
	}
}
