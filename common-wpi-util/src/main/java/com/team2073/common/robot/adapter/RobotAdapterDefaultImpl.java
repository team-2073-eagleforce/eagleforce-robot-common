package com.team2073.common.robot.adapter;

import com.team2073.common.robot.RobotDelegate;
import com.team2073.common.robot.RobotRunner;
import com.team2073.common.util.Ex;
import edu.wpi.first.wpilibj.TimedRobot;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * @author Preston Briggs
 */
public class RobotAdapterDefaultImpl extends LoggedRobot implements RobotAdapter {
    
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
    
    private final RobotRunner robotRunner;
    
    private RobotAdapterDefaultImpl(RobotDelegate robot) {
        super(robot.getPeriod());
        this.robotRunner = new RobotRunner(robot);
    }
    
    @Override
    public void robotInit() {
        robotRunner.robotInit();
    }

    @Override
    public void simulationPeriodic() {
        robotRunner.simulationPeriodic();
    }

    @Override
    public void simulationInit() {
        robotRunner.simulationInit();
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
    public RobotRunner getRobotRunner() {
        return robotRunner;
    }
}
