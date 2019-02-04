package com.team2073.common.robot.adapter;

import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * @author Preston Briggs
 */
public class SchedulerAdapterDefaultImpl implements SchedulerAdapter {
    
    private Scheduler scheduler = Scheduler.getInstance();
    private static SchedulerAdapterDefaultImpl instance = new SchedulerAdapterDefaultImpl();
    
    public static SchedulerAdapter getInstance() {
        return instance;
    }
    
    @Override
    public void run() {
        scheduler.run();
    }
}
