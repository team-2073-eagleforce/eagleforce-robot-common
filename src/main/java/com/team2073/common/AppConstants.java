package com.team2073.common;

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

	/**
	 * Units are in terms of RPM, Amps, watts, and inch pounds
	 * 
	 * kv units are kt units are inch pounds per amp
	 */
	public abstract class Motors {

		public abstract class Pro {
			public static final double FREE_SPEED_RPM = 18730.;
			public static final double FREE_CURRENT = .7;
			public static final double MAXIMUM_POWER = 347;
			public static final double STALL_TORQUE = 6.28;
			public static final double STALL_CURRENT = 134;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12 / STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
					/ (12 - RESISTANCE * FREE_CURRENT);
		}

		public abstract class Bag {
			public static final double FREE_SPEED_RPM = 13180;
			public static final double FREE_CURRENT = 1.8;
			public static final double MAXIMUM_POWER = 149;
			public static final double STALL_TORQUE = 3.81;
			public static final double STALL_CURRENT = 53;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12 / STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
					/ (12 - RESISTANCE * FREE_CURRENT);
		}

		public abstract class Cim {
			public static final double FREE_SPEED_RPM = 5330;
			public static final double FREE_CURRENT = /* 2.7 */1.17;
			public static final double MAXIMUM_POWER = 337;
			public static final double STALL_TORQUE = 21.33;
			public static final double STALL_CURRENT = 131;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12 / STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
					/ (12 - RESISTANCE * FREE_CURRENT);
		}

		public abstract class MiniCim {
			public static final double FREE_SPEED_RPM = 5840;
			public static final double FREE_CURRENT = 3;
			public static final double MAXIMUM_POWER = 215;
			public static final double STALL_TORQUE = 12.48;
			public static final double STALL_CURRENT = 89;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12 / STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM / 60) * Math.PI * 2)
					/ (12 - RESISTANCE * FREE_CURRENT);
		}

	}
}
