package com.team2073.common.subsys;

/**
 * @author pbriggs
 */
public class ExampleAppConstants {

    public abstract class DashboardKeys {
        public static final String INVERSE = "Inverse";
        public static final String SENSE = "Sense";
        public static final String RPM = "RPM";
        public static final String SET_F = "Set F";
        public static final String DRIVETRAIN_P = "Drive P";
        public static final String DRIVETRAIN_I = "Drive I";
        public static final String DRIVETRAIN_D = "Drive D";
        public static final String FGAIN = "Fgain";
        public static final String RIGHT_DRIVE_F_GAIN = "Right Drive F Gain";
        public static final String LEFT_DRIVE_F_GAIN = "Left Drive F Gain";
        public static final String INTAKE_P_GAIN = "Intake P";
        public static final String INTAKE_I_GAIN = "Intake I";
        public static final String INTAKE_D_GAIN = "Intake D";
        public static final String CAMERA_P = "Camera P";
        public static final String CAMERA_I = "Camera I";
        public static final String CAMERA_D = "Camera D";
    }

    public abstract class Defaults {
        public static final double DRIVETRAIN_FGAIN = .185; /* .324 */
        public static final double LEFT_DRIVE_F_GAIN = .15;
        public static final double RIGHT_DRIVE_F_GAIN = .15;
        public static final boolean LEFT_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean RIGHT_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;
        // good values for an interval of 10ms (MaxVel = 3154)
        public static final double DRIVETRAIN_P_GAIN = 0/*3.25*/;
        public static final double DRIVETRAIN_I_GAIN = 0;
        public static final double DRIVETRAIN_D_GAIN = 0/*10*/;
        public static final int MINIMUM_POINTS_TO_RUN = 20;
        public static final double INTAKE_P_GAIN = 0.9;
        public static final double INTAKE_I_GAIN = 0.0005;
        public static final double INTAKE_D_GAIN = 10;
        public static final double INTAKE_P_GAIN_ZEROING = 0.2;
        public static final double INTAKE_I_GAIN_ZEROING = 0.0005;
        public static final double INTAKE_D_GAIN_ZEROING = 30;

    }

    public abstract class Shooter {
        public static final String NAME = "Shooter";
        public static final String PIVOT_NAME = "Shooter Pivot";
        public static final double PIVOT_MAX_VELOCITY = 4300;
        public static final double PIVOT_MAX_ACCELERATION = 40;
        public static final double PIVOT_F_GAIN = 0;
        public static final double PIVOT_P_GAIN = 6.5;
        public static final double PIVOT_I_GAIN = 0;
        public static final double PIVOT_D_GAIN = 12;

        public static final double ENCODER_EDGES_PER_REVOLUTION = 2048;
        public static final double PIVOT_TO_ENCODER_RATIO = 1;
        public static final boolean PIVOT_DEFAULT_DIRECTION = true;
        public static final double PIVOT_HOLD_P = 6.5;
        public static final double PIVOT_HOLD_I = 0;
        public static final double PIVOT_HOLD_D = 12;

        /** @deprecated Use {@link ShooterPivotSubsystem.PivotAngles} instead. */
        public abstract class PivotAngles {

            public static final double INTAKE = 20;
            public static final double FRONT_FLAT = 10;
            public static final double BACK_FLAT = 160;
            public static final double FRONT_SHOOT = 25;
            public static final double BACK_SHOOT = 130;

        }

    }
}
