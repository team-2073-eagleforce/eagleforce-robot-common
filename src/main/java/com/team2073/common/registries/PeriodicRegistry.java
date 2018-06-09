package com.team2073.common.registries;

import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.assertion.Assert;
import com.team2073.common.registries.interfaces.PeriodicAware;
import com.team2073.common.registries.interfaces.SmartDashboardAware;
import com.team2073.common.util.ExceptionUtil;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PeriodicRegistry implements SmartDashboardAware {
	
	private static PeriodicRegistry singleton = new PeriodicRegistry();
	private static final Logger logger = LoggerFactory.getLogger(PeriodicRegistry.class);
	private final LinkedHashSet<PeriodicInstance> instanceList = new LinkedHashSet<>();
	private Timer innerTimer = new Timer();
	private Timer outerTimer = new Timer();
	private Timer overallTimer = new Timer();

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
	
	public static void runPeriodic() {
		singleton.overallTimer.reset();
		
		logger.debug("Starting periodic.");
		singleton.overallTimer.start();
		
		singleton.runPeriodicInternal();
		
		singleton.overallTimer.stop();
		singleton.overallTotal = singleton.overallTimer.get();
		logger.debug("Completed periodic [{}].", singleton.overallTotal);
	}

	public void runPeriodicInternal() {
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
			
			double elapsed = innerTimer.get();
			innerTimer.reset();
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
		
		logger.debug("Periodic loop: Total [{}] Avg [{}] Longest [{}:{}].", total, avg, longestInstance.last, longestInstance.name);
	}
	
	public static void registerSmartDashboard(SmartDashboardAwareRegistry registry) {
		singleton.registerSmartDashboardInternal(registry);
	}
	
	public void registerSmartDashboardInternal(SmartDashboardAwareRegistry registry) {
		registry.registerInstance(this);
	}
	
	@Override
	public void updateSmartDashboard() {
		// TODO Auto-generated method stub
		SmartDashboard.putNumber("periodic.overall.total", overallTotal);
		
		SmartDashboard.putNumber("periodic.curr.count", currCount);
		SmartDashboard.putNumber("periodic.curr.total", currTotal);
		SmartDashboard.putNumber("periodic.curr.avg", currAvg);
		if(currLongestInstance != null) {
			SmartDashboard.putNumber("periodic.curr.longest-instance.longest", currLongest);
			SmartDashboard.putString("periodic.curr.longest-instance.name", currLongestInstance.name);
			SmartDashboard.putNumber("periodic.curr.longest-instance.avg", currLongestInstance.average);
		}

		SmartDashboard.putNumber("periodic.history.count", runningCount);
		SmartDashboard.putNumber("periodic.history.total", runningTotal);
		SmartDashboard.putNumber("periodic.history.avg", runningAvg);
		if(runningLongestInstance != null) {
			SmartDashboard.putNumber("periodic.history.longest-instance.longest", runningLongest);
			SmartDashboard.putString("periodic.history.longest-instance.name", runningLongestInstance.name);
			SmartDashboard.putNumber("periodic.history.longest-instance.avg", runningLongestInstance.average);
		}
	}

	@Override
	public void readSmartDashboard() {
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
