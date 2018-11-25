package com.team2073.common.periodic;


import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ExceptionUtil;

import java.util.LinkedList;

public class OccasionalLoggingRunner implements AsyncPeriodicRunnable {

    private LinkedList<OccasionalLoggingAware> instanceList = new LinkedList<>();

    public OccasionalLoggingRunner() {
        autoRegisterWithPeriodicRunner(RobotContext.getInstance().getCommonProps().getLoggingAsyncPeriod());
    }

    public void register(OccasionalLoggingAware instance) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
    }

    @Override
    public void onPeriodicAsync() {
        instanceList.forEach(instance ->
                ExceptionUtil.suppressVoid(instance::occasionalLogging, instance.getClass().getSimpleName() + " ::occasionalLogging"));
    }

}