package com.team2073.common.periodic;


import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ExceptionUtil;

import java.util.LinkedList;

public class OccasionalLoggingRunner implements PeriodicRunnable {

    private LinkedList<OccasionalLoggingAware> instanceList = new LinkedList<>();

    public void register(OccasionalLoggingAware instance) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
    }

    public void onPeriodic() {
        instanceList.forEach(instance ->
                ExceptionUtil.suppressVoid(instance::occasionalLogging, instance.getClass().getSimpleName() + " ::occasionalLogging"));
    }

    @Override
    public void registerSelf(PeriodicRunner periodicRunner) {
        periodicRunner.registerAsync(this, RobotContext.getInstance().getCommonProps().getLoggingAsyncPeriod());
    }
}