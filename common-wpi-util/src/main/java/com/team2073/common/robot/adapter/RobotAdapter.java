package com.team2073.common.robot.adapter;

import com.team2073.common.robot.RobotRunner;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * @author Preston Briggs
 */
public interface RobotAdapter {
    
    /** See {@link IterativeRobot#robotInit()} */
    default void robotInit() {
    }
    
    /** See {@link IterativeRobot#disabledInit()} */
    default void disabledInit() {
    }
    
    /** See {@link IterativeRobot#autonomousInit()} */
    default void autonomousInit() {
    }
    
    /** See {@link IterativeRobot#teleopInit()} */
    default void teleopInit() {
    }
    
    /** See {@link IterativeRobot#testInit()} */
    default void testInit() {
    }
    
    /** See {@link IterativeRobot#robotPeriodic()} */
    default void robotPeriodic() {
    }
    
    /** See {@link IterativeRobot#disabledPeriodic()} */
    default void disabledPeriodic() {
    }
    
    /** See {@link IterativeRobot#autonomousPeriodic()} */
    default void autonomousPeriodic() {
    }
    
    /** See {@link IterativeRobot#teleopPeriodic()} */
    default void teleopPeriodic() {
    }
    
    /** See {@link IterativeRobot#testPeriodic()} */
    default void testPeriodic() {
    }
    
    /** See {@link IterativeRobot#free()} */
    default void free() {
    }
    
    RobotRunner getRobotRunner();
}
