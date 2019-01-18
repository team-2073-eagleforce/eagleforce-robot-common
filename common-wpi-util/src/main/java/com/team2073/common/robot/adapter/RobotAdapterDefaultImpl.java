package com.team2073.common.robot.adapter;

import com.team2073.common.robot.RobotDelegate;
import com.team2073.common.robot.RobotRunner;
import com.team2073.common.util.Ex;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * @author Preston Briggs
 */
public class RobotAdapterDefaultImpl extends TimedRobot implements RobotAdapter {
    
    private static RobotAdapterDefaultImpl instance;
    
    public static RobotAdapterDefaultImpl getInstance(RobotDelegate robot) {
        if (instance == null) {
            instance = new RobotAdapterDefaultImpl(robot);
        } else {
            if (instance.getRobotRunner() != robot) {
                throw Ex.illegalArg("Cannot call getInstance(RobotDelegate robot) with different RobotDelegate instances.");
            }
        }
        
        return instance;
    }
    
    private final RobotRunner robotDelegator;
    
    private RobotAdapterDefaultImpl(RobotDelegate robot) {
        this.robotDelegator = new RobotRunner(robot);
    }
    
    private RobotAdapterDefaultImpl(RobotDelegate robot, double period) {
        super(period);
        this.robotDelegator = new RobotRunner(robot);
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
    public RobotRunner getRobotRunner() {
        return robotDelegator;
    }
}
