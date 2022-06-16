package com.team2073.common.robot.adapter;

import com.team2073.common.robot.RobotRunner;
import edu.wpi.first.wpilibj.IterativeRobotBase;

/**
 * @author Preston Briggs
 */
public interface RobotAdapter {
    
    /** See {@link IterativeRobotBase#robotInit()} */
    default void robotInit() {
    }
    
    /** See {@link IterativeRobotBase#disabledInit()} */
    default void disabledInit() {
    }
    
    /** See {@link IterativeRobotBase#autonomousInit()} */
    default void autonomousInit() {
    }
    
    /** See {@link IterativeRobotBase#teleopInit()} */
    default void teleopInit() {
    }
    
    /** See {@link IterativeRobotBase#testInit()} */
    default void testInit() {
    }
    
    /** See {@link IterativeRobotBase#robotPeriodic()} */
    default void robotPeriodic() {
    }
    
    /** See {@link IterativeRobotBase#disabledPeriodic()} */
    default void disabledPeriodic() {
    }
    
    /** See {@link IterativeRobotBase#autonomousPeriodic()} */
    default void autonomousPeriodic() {
    }
    
    /** See {@link IterativeRobotBase#teleopPeriodic()} */
    default void teleopPeriodic() {
    }

    /** See {@link IterativeRobotBase#testPeriodic()} */
    default void testPeriodic() {
    }
    
    RobotRunner getRobotRunner();
}
