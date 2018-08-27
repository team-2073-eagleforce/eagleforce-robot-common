package com.team2073.common.periodic;


import com.team2073.common.assertion.Assert;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.Timer;

import java.util.LinkedList;

public class OccasionalLoggingRunner {
    private static LinkedList<OccasionalLoggingAware> instanceList = new LinkedList<>();
    private static Timer timer = new Timer();

    public static void registerInstance(OccasionalLoggingAware instance) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
        timer.start();
    }

    public static void startOccasionalLogging() {
        if(timer.hasWaited(5000)){
            timer.start();
            instanceList.forEach(instance ->
                    ExceptionUtil.suppressVoid(instance::occasionalLogging, instance.getClass().getSimpleName() + " ::occasionalLogging"));
        }
    }
}