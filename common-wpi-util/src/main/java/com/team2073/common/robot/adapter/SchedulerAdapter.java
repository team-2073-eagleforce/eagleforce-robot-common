package com.team2073.common.robot.adapter;


/**
 * @author Preston Briggs
 */
public interface SchedulerAdapter {
    
    public static SchedulerAdapter getInstance() {
        return SchedulerAdapterDefaultImpl.getInstance();
    }

    void run();
    
}
