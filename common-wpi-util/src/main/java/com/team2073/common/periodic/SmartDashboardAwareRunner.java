package com.team2073.common.periodic;

import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SmartDashboardAwareRunner implements AsyncPeriodicRunnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final RobotContext robotContext = RobotContext.getInstance();
	private final List<SmartDashboardAware> instanceList = new LinkedList<>();
	private boolean enabled = true;

	public SmartDashboardAwareRunner() {
		autoRegisterWithPeriodicRunner(robotContext.getCommonProps().getSmartDashboardAsyncPeriod());
	}

	public void registerInstance(SmartDashboardAware instance) {
		Assert.assertNotNull(instance, "instance");
		logger.info("Registered [{}] SmartDashboardAware instance.", instance.getClass().getSimpleName());
		instanceList.add(instance);
	}

	@Override
	public void onPeriodicAsync() {

		if(!robotContext.getCommonProps().getSmartDashboardAwareRunnerEnabled() || !enabled) {
			return;
		}

		updateAll();
		readAll();
	}

	private void updateAll() {
		instanceList.forEach(instance -> 
				ExceptionUtil.suppressVoid(instance::updateSmartDashboard, "SmartDashboardAware::updateSmartDashboard"));
	}

	private void readAll() {
		instanceList.forEach(instance -> 
				ExceptionUtil.suppressVoid(instance::readSmartDashboard, "SmartDashboardAware::readSmartDashboard"));
	}

	public void enable() {
		logger.info("SmartDashboardAwareRunner enabled");
		enabled = true;
	}

	public void disable() {
		logger.info("SmartDashboardAwareRunner disabled");
		enabled = false;

	}
}
