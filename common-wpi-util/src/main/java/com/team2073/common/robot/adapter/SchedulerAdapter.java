package com.team2073.common.robot.adapter;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Preston Briggs
 */
public interface SchedulerAdapter {
    
    public static SchedulerAdapter getInstance() {
        return SchedulerAdapterDefaultImpl.getInstance();
    }
    
    /**
     * Runs a single iteration of the loop. This method should be called often in order to have a
     * functioning {@link Command} system. The loop has five stages:
     *
     * <ol> <li>Poll the Buttons</li> <li>Execute/Remove the Commands</li> <li>Send values to
     * SmartDashboard</li> <li>Add Commands</li> <li>Add Defaults</li> </ol>
     */
    void run();
    
}
