package com.team2073.common.robot.adapter;

import com.team2073.common.robot.AbstractRobotDelegator;
import com.team2073.common.robot.RobotDelegate;

/**
 * @author Preston Briggs
 */
public class RobotAdapterSimulationImpl implements RobotAdapter {
    
    private final AbstractRobotDelegator robotDelegator;
    
    public RobotAdapterSimulationImpl(RobotDelegate robot) {
        this.robotDelegator = new AbstractRobotDelegator(robot);
    }
    
    public RobotAdapterSimulationImpl(RobotDelegate robot, double period) {
        // TODO: Use period properly
//        super(period);
        this.robotDelegator = new AbstractRobotDelegator(robot);
    }
    
    @Override
    public void robotInit() {
        robotDelegator.robotInit();
    }
    
    @Override
    public void disabledInit() {
        robotDelegator.disabledInit();
    }
    
    @Override
    public void autonomousInit() {
        robotDelegator.autonomousInit();
    }
    
    @Override
    public void teleopInit() {
        robotDelegator.teleopInit();
    }
    
    @Override
    public void testInit() {
        robotDelegator.testInit();
    }
    
    @Override
    public void robotPeriodic() {
        robotDelegator.robotPeriodic();
    }
    
    @Override
    public void disabledPeriodic() {
        robotDelegator.disabledPeriodic();
    }
    
    @Override
    public void autonomousPeriodic() {
        robotDelegator.autonomousPeriodic();
    }
    
    @Override
    public void teleopPeriodic() {
        robotDelegator.teleopPeriodic();
    }
    
    @Override
    public void testPeriodic() {
        robotDelegator.testPeriodic();
    }
    
    @Override
    public void free() {
        robotDelegator.free();
    }
    
    @Override
    public AbstractRobotDelegator getRobotDelegator() {
        return robotDelegator;
    }
    
}
