package com.team2073.common.periodic;

import com.team2073.common.CommonConstants;
import com.team2073.common.assertion.Assert;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRunner;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;

public class PeriodicRunner implements SmartDashboardAware, PeriodicAware {
	
	private static PeriodicRunner singleton = new PeriodicRunner();
	private static final Logger logger = LoggerFactory.getLogger(PeriodicRunner.class);
	private final LinkedHashSet<PeriodicInstance> instanceList = new LinkedHashSet<>();
	private Timer innerTimer = new Timer();
	private Timer outerTimer = new Timer();
	private Timer overallTimer = new Timer();
	private static NumberFormat formatter = new DecimalFormat("#0.00000");     
	
	private double overallTotal;
	
	private double currCount = 0;
	private double currTotal = 0;
	private double currAvg = 0;
	private double currLongest = 0;
	private PeriodicInstance currLongestInstance;
	
	private double runningCount = 0;
	private double runningTotal = 0;
	private double runningAvg = 0;
	private double runningLongest = 0;
	private PeriodicInstance runningLongestInstance;
	
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

		logger.trace("Starting periodic.");
		overallTimer.start();

		onPeriodicInternal();

		overallTimer.stop();

		overallTotal = overallTimer.getElapsedTime();

		// TODO: Sometimes this is 3.0 when the inner loop is 0.0. See if this is a formatting or a performance issue.
		logger.trace("Completed periodic [{}].", fmt(overallTotal));
	}

	public void onPeriodicInternal() {
		int count = 0;
		double total = 0;
		double avg = 0;
		PeriodicInstance longestInstance = null;
		
		currCount++;
		
		outerTimer.start();
		for (PeriodicInstance wrapper : instanceList) {
			PeriodicAware instance = wrapper.instance;
			count++;
			runningCount++;
			
			innerTimer.start();
			ExceptionUtil.suppressVoid(instance::onPeriodic, wrapper.name + " ::onPeriodic");
			innerTimer.stop();
			
			double elapsed = innerTimer.getElapsedTime();
			if(elapsed >= CommonConstants.Diagnostics.LONG_ON_PERIODIC_CALL) {
				logger.debug("[{}] long onPeriodic call on [{}]", elapsed, wrapper.getClass().getSimpleName());
			}
			innerTimer.stop();
			wrapper.update(elapsed);
			
			if(longestInstance == null || elapsed > longestInstance.last) {
				longestInstance = wrapper;
			}
			
			total += elapsed;
			runningTotal += elapsed;
		}
		outerTimer.stop();

		if(runningLongestInstance == null || longestInstance.last > runningLongest) {
			runningLongest = longestInstance.last;
			runningLongestInstance = longestInstance;
		}
		
		currLongest = total;
		currTotal += total;
		currLongestInstance = longestInstance;
		avg = total / count;
		currAvg = currTotal / currCount;
		runningAvg = currAvg / count;
		
		logger.trace("Periodic loop: Total [{}] Avg [{}] Longest [{}:{}].", fmt(total), fmt(avg), fmt(longestInstance.last), longestInstance.name);
		
		if(total >= CommonConstants.Diagnostics.LONG_PERIODIC_LOOP) {
			logger.debug("[{}] long periodic loop.", total);
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
		// TODO Auto-generated method stub
		SmartDashboard.putNumber("periodic.overall.total", fmt(overallTotal));
		
		SmartDashboard.putNumber("periodic.curr.count", fmt(currCount));
		SmartDashboard.putNumber("periodic.curr.total", fmt(currTotal));
		SmartDashboard.putNumber("periodic.curr.avg", fmt(currAvg));
		if(currLongestInstance != null) {
			SmartDashboard.putNumber("periodic.curr.longest-instance.longest", fmt(currLongest));
			SmartDashboard.putString("periodic.curr.longest-instance.name", currLongestInstance.name);
			SmartDashboard.putNumber("periodic.curr.longest-instance.avg", fmt(currLongestInstance.average));
		}

		SmartDashboard.putNumber("periodic.history.count", fmt(runningCount));
		SmartDashboard.putNumber("periodic.history.total", fmt(runningTotal));
		SmartDashboard.putNumber("periodic.history.avg", fmt(runningAvg));
		if(runningLongestInstance != null) {
			SmartDashboard.putNumber("periodic.history.longest-instance.longest", fmt(runningLongest));
			SmartDashboard.putString("periodic.history.longest-instance.name", runningLongestInstance.name);
			SmartDashboard.putNumber("periodic.history.longest-instance.avg", fmt(runningLongestInstance.average));
		}
	}

	@Override
	public void readSmartDashboard() {
	}

	public static PeriodicRunner getInstance() {
		return singleton;
	}

	private static class PeriodicInstance {
		private PeriodicAware instance;
		private String name;
		
		private double total;
		private double longest;
		private double last;
		private double count;
		private double average;
		
		public PeriodicInstance(PeriodicAware instance, String name) {
			this.instance = instance;
			this.name = name;
		}

		public void update(double time) {
			total += time;
			longest = time > longest ? time : longest;
			last = time;
			count++;
			average = total / count;
		}
	}
}
