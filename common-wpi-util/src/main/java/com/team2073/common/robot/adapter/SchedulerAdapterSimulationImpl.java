package com.team2073.common.robot.adapter;

/**
 * @author Preston Briggs
 */
public class SchedulerAdapterSimulationImpl implements SchedulerAdapter {
    
    private static SchedulerAdapterSimulationImpl instance = new SchedulerAdapterSimulationImpl();
    
    public static SchedulerAdapter getInstance() {
        return instance;
    }
    
    @Override
    public void run() {
        // do nothing
    }
}
