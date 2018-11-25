package com.team2073.common.periodic;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.CommonConstants;
import com.team2073.common.assertion.Assert;
import com.team2073.common.config.CommonProperties;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.Throw;
import com.team2073.common.util.Timer;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.team2073.common.util.ClassUtil.*;
import static com.team2073.common.util.ThreadUtil.*;

/**
 * Manages periodically invoking instances of {@link PeriodicRunnable}.
 *
 * <h3>Usage</h3>
 * <ol>
 *     <li>
 *         Register instances using either:
 *         <ul>
 *             <li>{@link #register(PeriodicRunnable, String)}</li>
 *             <li>{@link #registerAsync(AsyncPeriodicRunnable, String, long)}</li>
 *         </ul>
 *     </li>
 *     <li>
 *         Invoke this class by either:
 *         <ul>
 *             <li>Calling {@link PeriodicRunner#invokePeriodicInstances()} <b>repeatedly</b> from your Robot's {@link IterativeRobotBase#robotPeriodic()} method</li>
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
 * The total elapsed time of a {@link PeriodicRunnable#onPeriodic()} iteration is captured and instances consuming too much
 * time are logged. Averages and total iteration counts are also recorded and logged. All of this information is available
 * on the SmartDashboard. This only occurs for non-async instances on the main robot thread ({@link #register(PeriodicRunnable)})
 * since elapsed time of instances in the async thread pool do not matter (a long running instance will not affect other instances).
 *
 * <h3>Asynchronous Registration</h3>
 * Instances can be registered as async which will add them to a separate thread pool so they do not consume time on the
 * main robot thread. This is useful for things that are low priority, are time consuming, or do not need to happen in
 * a specific/deterministic order such as updating the SmartDashboard. The interval (period) at which the instance should
 * be called can be set using {@link #registerAsync(AsyncPeriodicRunnable, long)}.
 *
 * <h3>Circuit Breaker</h3>
 * Instances that throw exceptions will be monitored and a "circuit breaker" will be applied if necessary. Basically we
 * will stop invoking the instance and give it a chance to 'cool down' and self-correct. The duration that a circuit
 * breaker is applied to an instance will increase exponentially allowing short-lived issues to be resolved quickly
 * and limiting excessive log output of long-lived issues.
 *
 * @author pbriggs
 */
public class PeriodicRunner implements SmartDashboardAware {

	// TODO:
    // 	-Allow customizing the thread pool size (extract to properties)
    // 	-Extract to properties
	// 	-Write tests to verify the instance sets are not accepting duplicates
	//	-Redesign the pattern for instances registering themselves (registerSelf(PeriodicRunner runner))
	//		-Have instances call autoRegister(...) to register in constructor and then PeriodicRunner will check Common props to see if auto register is enabled (and if periodic runner is even enabled)
	//		-An alternative method register(...) will just check if periodic runner is enabled
	// 		-Create a method to check if an instance is already registered
	// -First time running, print a list of the instances so we can see what order they are being called in (include async with their interval)
	// -COMMON-200: Extract the section of code to run an instance and return a data object about the run
	// -Implement circuit breaker


    public static final long DEFAULT_ASYNC_PERIOD = 20;
    public static final long DEFAULT_SYNC_PERIOD = 20;

	private static final Logger logger = LoggerFactory.getLogger(PeriodicRunner.class);

	private final Map<PeriodicRunnable, PeriodicInstance> instanceMap = new HashMap<>();
	private final Map<AsyncPeriodicRunnable, AsyncPeriodicInstance> asyncInstanceMap = new HashMap<>();
	private Timer instanceLoopTimer = new Timer();
	private Timer fullLoopTimer = new Timer();
	private Timer overallTimer = new Timer();
	private boolean started;
	private boolean loggedEmptyList;
	private NumberFormat formatter = new DecimalFormat("#0.00000");
	
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

	// Sync-registration
	// ============================================================
	/** See {@link #autoRegister(PeriodicRunnable, String)} */
	public void autoRegister(PeriodicRunnable instance) {
		autoRegister(instance, simpleNameSafe(instance));
	}

	/** See {@link #register(PeriodicRunnable, String)} */
	public void register(PeriodicRunnable instance) {
		register(instance, simpleNameSafe(instance));
	}

	/**
	 * Registers this {@link PeriodicRunnable} only if auto register in enabled (controlled via
	 * {@link CommonProperties#getPeriodicRunnerAutoRegister()}). <br/>
	 * <br/>
	 * This is meant to be called from the constructor of every {@link PeriodicRunnable}. This way, instances
	 * do not need to be registered manually but if for some reason, manual registration is preferred, the property
	 * can be set to false and these auto registrations will be ignored so you can register manually.<br/>
	 * <br/>
	 * There is a convenience method to handle this: {@link PeriodicRunnable#autoRegisterWithPeriodicRunner()}.
	 */
	public void autoRegister(PeriodicRunnable instance, String name) {
		if (RobotContext.getInstance().getCommonProps().getPeriodicRunnerAutoRegister())
			register(instance, name);
		else
			logger.debug("Periodic Runner Auto Register is disabled. Ignoring registering [{}].", simpleNameSafe(instance));
	}

	/** Register an instance to be called periodically on the main robot thread. */
	public void register(PeriodicRunnable instance, String name) {
        Assert.assertNotNull(instance, "instance");
        logger.info("Registering periodic instance: [{}].", name);
        checkStarted(name);
        PeriodicInstance wrapper = new PeriodicInstance(instance, name);
        instanceMap.put(instance, wrapper);
        logger.debug("Registering periodic instance: [{}] complete.", name);
    }


	// Async-registration
	// ============================================================
	/** See {@link #autoRegister(PeriodicRunnable, String)} */
	public void autoRegisterAsync(AsyncPeriodicRunnable instance) {
		autoRegisterAsync(instance, DEFAULT_ASYNC_PERIOD);
	}

    /** @see #registerAsync(AsyncPeriodicRunnable, String, long)  */
    public void registerAsync(AsyncPeriodicRunnable instance) {
        registerAsync(instance, DEFAULT_ASYNC_PERIOD);
    }

	/** See {@link #autoRegister(PeriodicRunnable, String)} */
	public void autoRegisterAsync(AsyncPeriodicRunnable instance, long period) {
		autoRegisterAsync(instance, simpleNameSafe(instance), period);
	}

    /** @see #registerAsync(AsyncPeriodicRunnable, String, long)  */
    public void registerAsync(AsyncPeriodicRunnable instance, long period) {
        registerAsync(instance, simpleNameSafe(instance), period);
    }

	/** See {@link #autoRegister(PeriodicRunnable, String)} */
	public void autoRegisterAsync(AsyncPeriodicRunnable instance, String name) {
		autoRegisterAsync(instance, name, DEFAULT_ASYNC_PERIOD);
	}

    /** @see #registerAsync(AsyncPeriodicRunnable, String, long)  */
    public void registerAsync(AsyncPeriodicRunnable instance, String name) {
        registerAsync(instance, name, DEFAULT_ASYNC_PERIOD);
    }

	/** See {@link #autoRegister(PeriodicRunnable, String)} */
	public void autoRegisterAsync(AsyncPeriodicRunnable instance, String name, long period) {
		if (RobotContext.getInstance().getCommonProps().getPeriodicRunnerAutoRegister())
			registerAsync(instance, name);
		else
			logger.debug("Periodic Runner Auto Register is disabled. Ignoring registering [{}].", simpleNameSafe(instance));
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
	public void registerAsync(AsyncPeriodicRunnable instance, String name, long period) {
		Assert.assertNotNull(instance, "instance");
        logger.info("Registering ASYNC periodic instance: [{}].", name);
		checkStarted(name);
        AsyncPeriodicInstance wrapper = new AsyncPeriodicInstance(instance, name, period);
        asyncInstanceMap.put(instance, wrapper);
        logger.debug("Registering ASYNC periodic instance: [{}] complete.", name);
	}


	// Other public methods
	// ============================================================
	public boolean isRegistered(PeriodicRunnable instance) {
		return instanceMap.containsKey(instance);
	}

	public boolean isRegistered(AsyncPeriodicRunnable instance) {
		return asyncInstanceMap.containsKey(instance);
	}

	private void checkStarted(String instanceName) {
		if (started)
			Throw.illegalState(String.format("Cannot register instance [%s]. [%s] already started.",
					instanceName, getClass().getSimpleName()));
	}

	private double fmt(double number) {
		 return Double.parseDouble(formatter.format(number));
	}

	// Runnable invocation methods
	// ============================================================
    public void startPeriodicLoop() {
	    startPeriodicLoop(DEFAULT_SYNC_PERIOD);
    }

	public void startPeriodicLoop(long period) {
	    // TODO
        throw new NotYetImplementedException("startPeriodicLoop");
    }

	public void invokePeriodicInstances() {
		overallTimer.stop();
		logger.trace("Running periodic loop...");

		overallTimer.start();
		invokePeriodicInstancesInternal();
		overallTimer.stop();

		logger.trace("Running periodic loop complete. Total duration: [{}]", overallTimer.getElapsedTime());
	}

	private void invokePeriodicInstancesInternal() {
		if (!started) {
            startAsyncThread();
            started = true;
        }

		if (instanceMap.isEmpty()) {
			if (!loggedEmptyList) {
				loggedEmptyList = true;
				logger.info("No periodic instance to loop over. Skipping...");
			}
			return;
		}

		currInstanceLoopHistory = new InstanceAwareDurationHistory();
		fullLoopTimer.start();

		for (PeriodicInstance wrapper : instanceMap.values()) {
			PeriodicRunnable instance = wrapper.instance;
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
					currInstanceLoopHistory.getTotal(), instanceMap.size());
		}
	}

	private void startAsyncThread() {
	    logger.info("Starting asynchronous thread pool.");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10, withThreadNamePattern("periodic-%d"));
        for (AsyncPeriodicInstance instance : asyncInstanceMap.values()) {
            logger.info("Adding [{}] to ASYNC thread at an interval of [{}] ms.", instance.getName(), instance.getPeriod());
            scheduler.scheduleAtFixedRate(
					new AsyncRunnable(instance), 20, instance.getPeriod(), TimeUnit.MILLISECONDS);
        }
        logger.info("Starting asynchronous thread pool completed.");
    }

	public void registerSmartDashboard(SmartDashboardAwareRunner registry) {
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


	// Testing methods
	// ============================================================

	/** @see #fullLoopHistory */
	@VisibleForTesting
	InstanceAwareDurationHistory getFullLoopHistory() {
		return fullLoopHistory;
	}

	/** @see #instanceLoopHistory */
	@VisibleForTesting
	InstanceAwareDurationHistory getInstanceLoopHistory() {
		return instanceLoopHistory;
	}

	/** @see #currInstanceLoopHistory */
	@VisibleForTesting
	InstanceAwareDurationHistory getCurrInstanceLoopHistory() {
		return currInstanceLoopHistory;
	}

	// Inner classes
	// ============================================================

	private static abstract class BaseRegistration<T> {

		protected final T instance;
		private final DurationHistory history = new DurationHistory();
		private final String name;

		public BaseRegistration(T instance, String name) {
			this.instance = instance;
			this.name = name;
		}

		public void update(long time) {
			history.update(time);
		}

		public double getAverage() {
			return history.getAverage();
		}

		public T getInstance() {
			return instance;
		}

		public DurationHistory getHistory() {
			return history;
		}

		public String getName() {
			return name;
		}

		// I don't think we need this now that it is not a key in the map

//		@Override
//		public boolean equals(Object o) {
//			if (this == o) return true;
//			if (o == null || getClass() != o.getClass()) return false;
//			PeriodicInstance that = (PeriodicInstance) o;
//			return Objects.equal(instance, that.instance);
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hashCode(instance);
//		}
	}

    // TODO: Change name to PeriodicRegistration to match pattern of RecordableRegistration
	private static class PeriodicInstance extends BaseRegistration<PeriodicRunnable> {

		public PeriodicInstance(PeriodicRunnable instance, String name) {
			super(instance, name);
		}

	}

    private static class AsyncPeriodicInstance extends BaseRegistration<AsyncPeriodicRunnable> {

	    private final long period;

        public AsyncPeriodicInstance(AsyncPeriodicRunnable instance, String name, long period) {
            super(instance, name);
            this.period = period;
        }

        public long getPeriod() {
            return period;
        }

//		@Override
//		public boolean equals(Object o) {
//			if (this == o) return true;
//			if (o == null || getClass() != o.getClass()) return false;
//			if (!super.equals(o)) return false;
//			AsyncPeriodicInstance that = (AsyncPeriodicInstance) o;
//			return period == that.period;
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hashCode(super.hashCode(), period);
//		}
	}

	private static class DurationHistory {
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

	@VisibleForTesting
	static class InstanceAwareDurationHistory extends DurationHistory {

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

		private AsyncPeriodicInstance instance;

		public AsyncRunnable(AsyncPeriodicInstance instance) {
			this.instance = instance;
		}

		@Override
		public void run() {
			ExceptionUtil.suppressVoid(instance.instance::onPeriodicAsync, "instance::onPeriodicAsync");
		}
	}
}
