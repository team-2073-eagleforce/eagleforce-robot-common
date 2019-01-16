package com.team2073.common.robot;

import com.team2073.common.robot.adapter.RobotAdapterDefaultImpl;
import edu.wpi.first.wpilibj.RobotBase;

import java.util.function.Supplier;

/**
 * @author Preston Briggs
 */
public class RobotRunner {
    
    public static <T extends RobotDelegate> void start(Supplier<T> robotDelegate) {
        start(robotDelegate.get());
    }
    
    public static void start(RobotDelegate robotDelegate) {
        RobotAdapterDefaultImpl robotAdapter = RobotAdapterDefaultImpl.getInstance(robotDelegate);
        RobotBase.startRobot(() -> robotAdapter);
    }
    
    public static void startSimulation(RobotDelegate robotDelegate) {
    
    }
}
