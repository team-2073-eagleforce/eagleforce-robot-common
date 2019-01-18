package com.team2073.common.robot.adapter;

import com.team2073.common.robot.RobotDelegate;
import com.team2073.common.robot.RobotRunner;

/**
 * @author Preston Briggs
 */
public class RobotAdapterSimulationImpl implements RobotAdapter {
    
    private final RobotRunner robotRunner;
    
    public RobotAdapterSimulationImpl(RobotDelegate robot) {
        this.robotRunner = new RobotRunner(robot);
    }
    
    public RobotAdapterSimulationImpl(RobotDelegate robot, double period) {
        // TODO: Use period properly
//        super(period);
        this.robotRunner = new RobotRunner(robot);
    }
    
    @Override
    public void robotInit() {
        robotRunner.robotInit();
    }
    
    @Override
    public void disabledInit() {
        robotRunner.disabledInit();
    }
    
    @Override
    public void autonomousInit() {
        robotRunner.autonomousInit();
    }
    
    @Override
    public void teleopInit() {
        robotRunner.teleopInit();
    }
    
    @Override
    public void testInit() {
        robotRunner.testInit();
    }
    
    @Override
    public void robotPeriodic() {
        robotRunner.robotPeriodic();
    }
    
    @Override
    public void disabledPeriodic() {
        robotRunner.disabledPeriodic();
    }
    
    @Override
    public void autonomousPeriodic() {
        robotRunner.autonomousPeriodic();
    }
    
    @Override
    public void teleopPeriodic() {
        robotRunner.teleopPeriodic();
    }
    
    @Override
    public void testPeriodic() {
        robotRunner.testPeriodic();
    }
    
    @Override
    public void free() {
        robotRunner.free();
    }
    
    @Override
    public RobotRunner getRobotRunner() {
        return robotRunner;
    }
    
}
