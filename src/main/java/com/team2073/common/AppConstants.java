package org.usfirst.frc.team2073.robot.conf;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public abstract class AppConstants {
    public abstract class Controllers {
        public abstract class PowerStick {
            public abstract class ButtonPorts {
                public static final int LEFT = 4;
                public static final int CENTER = 3;
                public static final int BOTTOM = 2;
                public static final int TRIGGER = 1;
            }
        }

        public abstract class DriveWheel {
            public abstract class ButtonPorts {
                public static final int LPADDLE = 1;
                public static final int RPADDLE = 3;
            }
        }

        public abstract class Xbox {
            public abstract class ButtonPorts {
                public static final int A = 1;
                public static final int B = 2;
                public static final int X = 3;
                public static final int Y = 4;
                public static final int L1 = 5;
                public static final int R1 = 6;
                public static final int BACK = 7;
                public static final int START = 8;
            }

            public abstract class Axes {
                public static final int LEFT_X = 0;
                public static final int LEFT_Y = 1;
                public static final int LEFT_TRIGGER = 2;
                public static final int RIGHT_TRIGGER = 3;
                public static final int RIGHT_X = 4;
                public static final int RIGHT_Y = 5;

            }
        }
    }

    public abstract class Diagnostics {
        public static final double UNSAFE_BATTERY_VOLTAGE = 8.0;
        public static final double LONG_ON_PERIODIC_CALL = 5e-3;
        public static final double LONG_PERIODIC_LOOP = 30e-3;
    }
}
