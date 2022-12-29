package com.team2073.common.robot.adapter;

//import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * @author Preston Briggs
 */
public class SchedulerAdapterDefaultImpl implements SchedulerAdapter {
    
    private CommandScheduler scheduler = CommandScheduler.getInstance();
    private static SchedulerAdapterDefaultImpl instance = new SchedulerAdapterDefaultImpl();
    
    public static SchedulerAdapter getInstance() {
        return instance;
    }
    
    @Override
    public void run() {
        scheduler.run();
    }
}
