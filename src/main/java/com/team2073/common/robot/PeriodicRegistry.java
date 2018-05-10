package com.team2073.common.robot;

import com.team2073.common.assertion.Assert;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.util.ExceptionUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.conf.AppConstants;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;

public class PeriodicRegistry implements SmartDashboardAware {
	
	private static PeriodicRegistry singleton = new PeriodicRegistry();
	private static final Logger logger = LoggerFactory.getLogger(PeriodicRegistry.class);
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
	
	public static void runPeriodic() {
		singleton.overallTimer.reset();
		
		logger.debug("Starting periodic.");
		singleton.overallTimer.start();
		
		singleton.runPeriodicInternal();
		
		singleton.overallTimer.stop();
		
		singleton.overallTotal = singleton.overallTimer.get();
		logger.debug("Completed periodic [{}].", fmt(singleton.overallTotal));
	}
	
	public static double fmt(double number) {
		 return Double.parseDouble(formatter.format(number));
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
			if(elapsed >= AppConstants.Diagnostics.LONG_ON_PERIODIC_CALL) {
				logger.debug("[{}}] long onPeriodic call on [{}]", elapsed, wrapper.getClass().getSimpleName());
			}
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
		
		logger.debug("Periodic loop: Total [{}] Avg [{}] Longest [{}:{}].", fmt(total), fmt(avg), fmt(longestInstance.last), longestInstance.name);
		
		if(total >= AppConstants.Diagnostics.LONG_PERIODIC_LOOP) {
			logger.debug("[{}}] long periodic loop on [{}]", total, instanceList.getClass().getName());
		}
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
