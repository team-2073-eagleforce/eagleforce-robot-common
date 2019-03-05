package com.team2073.common;

public abstract class CommonConstants {
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

	// TODO: Treat as defaults
	public abstract class Diagnostics {
		public static final double UNSAFE_BATTERY_VOLTAGE = 8.0;
		public static final long LONG_ON_PERIODIC_CALL = 10;
		public static final long LONG_PERIODIC_LOOP = 30;
	}

	public static abstract class TestTags {
		public static final String INTEGRATION_TEST = "integration-test";
	}
}
