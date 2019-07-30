package com.team2073.common.periodic;


import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static org.reflections.Reflections.log;

public class OccasionalLoggingRunner implements AsyncPeriodicRunnable {
    
    private final RobotContext robotContext = RobotContext.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(OccasionalLoggingRunner.class);
    private LinkedList<OccasionalLoggingAware> instanceList = new LinkedList<>();
    public boolean enabled = true;

    public OccasionalLoggingRunner() {
        autoRegisterWithPeriodicRunner(robotContext.getCommonProps().getLoggingAsyncPeriod());
    }

    public void register(OccasionalLoggingAware instance) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
    }

    @Override
    public void onPeriodicAsync() {

        if (!robotContext.getCommonProps().getOccasionalLoggingRunnerEnabled() || !enabled) {
            return;
        }

        instanceList.forEach(instance ->
                ExceptionUtil.suppressVoid(instance::occasionalLogging, instance.getClass().getSimpleName() + " ::occasionalLogging"));
    }

    public void enable(){
        if(enabled || robotContext.getCommonProps().getOccasionalLoggingRunnerEnabled()) {
            logger.info("OccasionalLoggingRunner enabled");
        }
        enabled = true;
    }

    public void disable() {
        if(!enabled){
            logger.info("OccasionalLoggingRunner disabled");
        }
        enabled = false;
    }

}