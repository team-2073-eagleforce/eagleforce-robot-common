package com.team2073.common.simulation.env;

import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.robot.DetailedRobotState.RobotMode;
import com.team2073.common.util.EnumUtil;

/**
 * @author pbriggs
 */
public class SimulationRobotRunner implements PeriodicAware {

    private SimulationRobot robot = new SimulationRobot();

    private boolean initialized = false;

    private RobotMode prevRobotMode = RobotMode.TELEOP;
    private boolean prevRobotEnabled = false;

    private RobotMode robotMode = RobotMode.TELEOP;
    private boolean robotEnabled = true;

    @Override
    public void onPeriodic() {
        if (!initialized) {
            initialized = true;
            robot.robotInit();
        }

        if (robotEnabled == false) {
            if (enabledChanged()) {
                robot.disabledInit();
            }
            prevRobotEnabled = robotEnabled;
            robot.disabledPeriodic();
        } else {
            switch (robotMode) {
                case TELEOP:
                    if (modeChanged())
                        robot.teleopInit();
                    robot.teleopPeriodic();
                    break;
                case AUTONOMOUS:
                    if (modeChanged())
                        robot.autonomousInit();
                    robot.autonomousPeriodic();
                    break;
                case TEST:
                    if (modeChanged())
                        robot.testInit();
                    robot.testPeriodic();
                    break;
                default:
                    EnumUtil.throwUnknownValueException(robotMode);
            }
            prevRobotMode = robotMode;
        }
        robot.robotPeriodic();
    }

    private boolean enabledChanged() {
        return prevRobotEnabled != robotEnabled;
    }

    private boolean modeChanged() {
        return prevRobotMode != robotMode;
    }

    // Getters/setters
    public void setMode(RobotMode robotMode) {
        this.robotMode = robotMode;
    }

    public void setEnabled(boolean robotEnabled) {
        this.robotEnabled = robotEnabled;
    }
}
