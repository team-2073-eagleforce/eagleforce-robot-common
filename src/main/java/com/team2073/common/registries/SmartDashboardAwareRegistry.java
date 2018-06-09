package com.team2073.common.registries;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.assertion.Assert;
import com.team2073.common.registries.interfaces.PeriodicAware;
import com.team2073.common.registries.interfaces.SmartDashboardAware;
import com.team2073.common.util.ExceptionUtil;

public class SmartDashboardAwareRegistry implements PeriodicAware {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<SmartDashboardAware> instanceList = new LinkedList<>();

	public SmartDashboardAwareRegistry() {
		PeriodicRegistry.registerInstance(this);
		PeriodicRegistry.registerSmartDashboard(this);
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

	public void updateAll() {
		instanceList.forEach(instance -> ExceptionUtil.suppressVoid(instance::updateSmartDashboard,
				"SmartDashboardAware::updateSmartDashboard"));
	}

	public void readAll() {
		instanceList.forEach(instance -> ExceptionUtil.suppressVoid(instance::readSmartDashboard,
				"SmartDashboardAware::readSmartDashboard"));
	}
}
