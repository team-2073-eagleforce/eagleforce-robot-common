package com.team2073.common.periodic;

import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SmartDashboardAwareRunner implements PeriodicRunnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<SmartDashboardAware> instanceList = new LinkedList<>();
	private final RobotContext robotCtx;

	public SmartDashboardAwareRunner() {
		this(RobotContext.getInstance());
	}

	public SmartDashboardAwareRunner(RobotContext robotCtx) {
		this.robotCtx = robotCtx;
	}

	public void registerInstance(SmartDashboardAware instance) {
		Assert.assertNotNull(instance, "instance");
		logger.info("Registered [{}] SmartDashboardAware instance.", instance.getClass().getSimpleName());
		instanceList.add(instance);
	}

	@Override
	public void onPeriodic() {
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

	@Override
	public void registerSelf(PeriodicRunner periodicRunner) {
		periodicRunner.registerAsync(this, robotCtx.getCommonProps().getSmartDashboardAsyncPeriod());
		periodicRunner.registerSmartDashboard(this);

	}
}
