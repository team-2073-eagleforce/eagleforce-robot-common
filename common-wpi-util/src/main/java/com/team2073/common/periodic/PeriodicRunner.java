package com.team2073.common.periodic;

import com.team2073.common.CommonConstants;
import com.team2073.common.assertion.Assert;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRunner;
import com.team2073.common.util.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;


public class PeriodicRunner implements SmartDashboardAware, PeriodicAware {

	private static final Logger logger = LoggerFactory.getLogger(PeriodicRunner.class);
	private static PeriodicRunner singleton = new PeriodicRunner();

	private final LinkedHashSet<PeriodicInstance> instanceList = new LinkedHashSet<>();
	private Timer instanceLoopTimer = new Timer();
	private Timer fullLoopTimer = new Timer();
	private Timer overallTimer = new Timer();
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

	public static void registerInstance(PeriodicAware instance) {
		Assert.assertNotNull(instance, "instance");
		registerInstance(instance, instance.getClass().getSimpleName());
	}

	public static void registerInstance(PeriodicAware instance, String name) {
		Assert.assertNotNull(instance, "instance");
		PeriodicInstance wrapper = new PeriodicInstance(instance, name);
		singleton.instanceList.add(wrapper);
		logger.info("Registered [{}] periodic instance.", wrapper.name);
	}

	/** @deprecated Use {@link #getInstance()#onPeriodic()} instead. */
	@Deprecated
	public static void runPeriodic() {
		getInstance().onPeriodic();
	}
	
	public static double fmt(double number) {
		 return Double.parseDouble(formatter.format(number));
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

	public void onPeriodicInternal() {
		currInstanceLoopHistory = new InstanceAwareDurationHistory();
		fullLoopTimer.start();

		for (PeriodicInstance wrapper : instanceList) {
			PeriodicAware instance = wrapper.instance;
			instanceLoopTimer.start();
//			ExceptionUtil.suppressVoid(instance::onPeriodic, wrapper.name + " ::onPeriodic");
			instance.onPeriodic();
			instanceLoopTimer.stop();
			
			long elapsed = instanceLoopTimer.getElapsedTime();
			if(elapsed >= CommonConstants.Diagnostics.LONG_ON_PERIODIC_CALL) {
				logger.trace("Long onPeriodic call of [{}] ms from [{}]", elapsed, wrapper.getClass().getSimpleName());
			}

			wrapper.update(elapsed);
			instanceLoopHistory.update(elapsed, wrapper);
			currInstanceLoopHistory.update(elapsed, wrapper);
		}
		fullLoopTimer.stop();
		fullLoopHistory.update(fullLoopTimer.getElapsedTime(), currInstanceLoopHistory.getLongestInstance());

		logger.trace("Periodic loop: Total: [{}] ms. Avg: [{}] ms. Longest: [{}]: ms. From: [{}].",
				currInstanceLoopHistory.getTotal(), fmt(currInstanceLoopHistory.getAverage()),
				currInstanceLoopHistory.getLongest(), currInstanceLoopHistory.getLongestInstance().name);
		
		if(currInstanceLoopHistory.getTotal() >= CommonConstants.Diagnostics.LONG_PERIODIC_LOOP) {
			logger.trace("Long periodic loop of [{}] ms. Number of instances looped [{}].",
					currInstanceLoopHistory.getTotal(), instanceList.size());
		}
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
			SmartDashboard.putString("periodic.curr.longest-instance.name", fullLoopHistory.getLongestInstance().name);
			SmartDashboard.putNumber("periodic.curr.longest-instance.avg", fullLoopHistory.getLongestInstance().getAverage());
		}

		SmartDashboard.putNumber("periodic.history.count", instanceLoopHistory.getCount());
		SmartDashboard.putNumber("periodic.history.total", instanceLoopHistory.getTotal());
		SmartDashboard.putNumber("periodic.history.avg", instanceLoopHistory.getAverage());
		if(instanceLoopHistory.getLongestInstance() != null) {
			SmartDashboard.putNumber("periodic.history.longest-instance.longest", instanceLoopHistory.getLongest());
			SmartDashboard.putString("periodic.history.longest-instance.name", instanceLoopHistory.getLongestInstance().name);
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

	public static class PeriodicInstance {
		private PeriodicAware instance;
		private DurationHistory history = new DurationHistory();
		private String name;

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
}
