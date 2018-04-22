package com.team2073.common.conf;

public abstract class AppConstants {
	public abstract class Controllers {
		public abstract class PowerStick {
			public static final int PORT = 1;

			public abstract class ButtonPorts {
				public static final int LEFT = 4;
			}
		}

		public abstract class DriveWheel {
			public static final int PORT = 2;

			public abstract class ButtonPorts {
				public static final int LPADDLE = 1;
			}
		}

		public abstract class Xbox {
			public static final int PORT = 0;

			public abstract class ButtonPorts {
				public static final int A = 1;
				public static final int B = 2;
				public static final int X = 3;
				public static final int Y = 4;
				public static final int L1 = 5;
				public static final int R1 = 6;
				public static final int L2 = 7;
				public static final int R2 = 8;
			}
		}
	}

	public abstract class RobotPorts {
		// Drivetrain
		public static final int LEFT_MOTOR = 7;
		public static final int LEFT_MOTOR_SLAVE = 8;
		public static final int RIGHT_MOTOR = 3;
		public static final int RIGHT_MOTOR_SLAVE = 1;
		public static final int DRIVE_SOLENOID_1 = 0;
		public static final int DRIVE_SOLENOID_2 = 7;
		public static final int INTAKEMOTOR = 0;

	}

	public abstract class DashboardKeys {
		public static final String INVERSE = "Inverse";
		public static final String SENSE = "Sense";
		public static final String RPM = "RPM";
		public static final String SET_F = "Set F";
		public static final String SET_P = "Set P";
		public static final String SET_I = "Set I";
		public static final String SET_D = "Set D";
		public static final String FGAIN = "Fgain";
		public static final String LEFT_DRIVE_F_GAIN = "LeftDrive FGain";
		public static final String RIGHT_DRIVE_F_GAIN = "RightDrive FGain";
	}

	public abstract class Defaults {
		public static final double FGAIN = 2;
		public static final double DEFAULT_INVERSE = .2;
		public static final double DEFAULT_SENSE = .7;
		public static final double LEFT_DRIVE_F_GAIN = 2;
		public static final double RIGHT_DRIVE_F_GAIN = 2;
		public static final boolean RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;
		public static final boolean RIGHT_MOTOR_DEFAULT_DIRECTION = true;
		public static final boolean LEFT_MOTOR_DEFAULT_DIRECTION = false;
		public static final boolean LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;

	}
	
	/**
	 * Units are in terms of RPM, Amps, watts, and inch pounds
	 * 
	 * kv units are 
	 * kt units are inch pounds per amp
	 */
	public abstract class Motors {

		public abstract class Pro {
			public static final double FREE_SPEED_RPM = 18730.;
			public static final double FREE_CURRENT = .7;
			public static final double MAXIMUM_POWER = 347;
			public static final double STALL_TORQUE = 6.28;
			public static final double STALL_CURRENT = 134;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12/STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM/60)*Math.PI*2)/(12-RESISTANCE*FREE_CURRENT);
		}

		public abstract class Bag {
			public static final double FREE_SPEED_RPM = 13180;
			public static final double FREE_CURRENT = 1.8;
			public static final double MAXIMUM_POWER = 149;
			public static final double STALL_TORQUE = 3.81;
			public static final double STALL_CURRENT = 53;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12/STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM/60)*Math.PI*2)/(12-RESISTANCE*FREE_CURRENT);
		}

		public abstract class Cim {
			public static final double FREE_SPEED_RPM = 5330;
			public static final double FREE_CURRENT = /*2.7*/1.17;
			public static final double MAXIMUM_POWER = 337;
			public static final double STALL_TORQUE = 21.33;
			public static final double STALL_CURRENT = 131;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12/STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM/60)*Math.PI*2)/(12-RESISTANCE*FREE_CURRENT);
		}

		public abstract class MiniCim {
			public static final double FREE_SPEED_RPM = 5840;
			public static final double FREE_CURRENT = 3;
			public static final double MAXIMUM_POWER = 215;
			public static final double STALL_TORQUE = 12.48;
			public static final double STALL_CURRENT = 89;
			public static final double MOTOR_KT = STALL_TORQUE / STALL_CURRENT;
			public static final double RESISTANCE = 12/STALL_CURRENT;
			public static final double MOTOR_KV = ((FREE_SPEED_RPM/60)*Math.PI*2)/(12-RESISTANCE*FREE_CURRENT);
		}

	}

	public abstract class Subsystems {
		public abstract class Drivetrain {
			public static final String NAME = "Drivetrain";
			public static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
//			In units of encoder tics / 100ms 
			public static final double AUTONOMOUS_MAX_VELOCITY = 2000 /*300*/;
//			In units of encoder tics / 100ms / 100ms
			public static final double AUTONOMOUS_MAX_ACCELERATION = 100/*50*/;
			public static final double ROBOT_WIDTH = 29;
			public static final double ENCODER_EDGES_PER_REVOLUTION = 4096;
			public static final double HIGH_GEAR_RATIO = 4.89;
			public static final double LOW_GEAR_RATIO = 15.41;

			public abstract class ComponentNames {
				public static final String LEFT_MOTOR = "Left Motor";
				public static final String LEFT_MOTOR_SLAVE = "Left Motor Slave";
				public static final String RIGHT_MOTOR = "Right Motor";
				public static final String RIGHT_MOTOR_SLAVE = "Right Motor Slave";
				public static final String SOLENOID_1 = "Solenoid 1";
				public static final String SOLENOID_2 = "Solenoid 2";
			}
		}

		public abstract class BallIntake {
			public static final String NAME = "Ball Intake";

			public abstract class ComponentNames {
				public static final String MOTOR_1 = "Motor 1";
				public static final String MOTOR_2 = "Motor 2";
				public static final String SOLENOID_1 = "Solenoid 1";
				public static final String SOLENOID_2 = "Solenoid 2";
			}
		}
	}
}
