package com.team2073.common.periodic;

import com.team2073.common.assertion.Assert;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.TimerUtil;

import java.util.LinkedList;

public class OccasionalLoggingRegistry {
    private static LinkedList<OccasionalLoggingAware> instanceList = new LinkedList<>();
    private static TimerUtil timerUtil = new TimerUtil();

    public static void registerInstance(OccasionalLoggingAware instance) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
        timerUtil.start();
    }

    public static void startOccasionalLogging() {
        if(timerUtil.hasWaited(5000)){
            timerUtil.start();
            instanceList.forEach(instance ->
                    ExceptionUtil.suppressVoid(instance::occasionalLogging, instance.getClass().getSimpleName() + " ::occasionalLogging"));
        }
    }
}