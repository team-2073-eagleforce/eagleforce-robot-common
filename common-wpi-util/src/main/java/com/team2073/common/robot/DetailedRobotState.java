package com.team2073.common.robot;

import edu.wpi.first.wpilibj.RobotState;

/**
 * @author pbriggs
 */
public enum DetailedRobotState {

    TELEOP_DISABLED(RobotMode.TELEOP, false),
    TELEOP_ENABLED(RobotMode.TELEOP, true),
    AUTON_DISABLED(RobotMode.AUTONOMOUS, false),
    AUTON_ENABLED(RobotMode.AUTONOMOUS, true),
    TEST_DISABLED(RobotMode.TEST, false),
    TEST_ENABLED(RobotMode.TEST, true);

    public static DetailedRobotState forState(RobotMode mode, boolean enabled) {
        for (DetailedRobotState value : DetailedRobotState.values()) {
            if (value.mode == mode && value.enabled == enabled) {
                return value;
            }
        }
        throw new IllegalStateException(String.format("No [%s] found for combination [%s]/[%s].",
                DetailedRobotState.class.getSimpleName(), mode, enabled));
    }

    public final RobotMode mode;
    public final boolean enabled;

    DetailedRobotState(RobotMode mode, boolean enabled) {
        this.mode = mode;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isTeleop() {
        return this.mode == RobotMode.TELEOP;
    }

    public boolean isAuton() {
        return this.mode == RobotMode.AUTONOMOUS;
    }

    public boolean isTest() {
        return this.mode == RobotMode.TEST;
    }

    public enum RobotMode {
        AUTONOMOUS {
            @Override
            public boolean isCurrentState() {
                return RobotState.isAutonomous();
            }
        },
        TELEOP {
            @Override
            public boolean isCurrentState() {
                return RobotState.isTeleop();
            }
        },
        TEST {
            @Override
            public boolean isCurrentState() {
                return RobotState.isTest();
            }
        };

        public abstract boolean isCurrentState();
    }


}
