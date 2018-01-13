package org.usfirst.frc.team2073.conf;

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
	}
	public abstract class Defaults {
		public static final double FGAIN = 0;
		public static final double DEFAULT_INVERSE = .2;
		public static final double DEFAULT_SENSE = .7;
		
	}
	public abstract class Subsystems {
		public abstract class Drivetrain {
			public static final String NAME = "Drivetrain";
			public static final double WHEEL_DIAMETER = 4 * Math.PI;
			public static final double AUTONOMOUS_MAX_VELOCITY = 300;
			public static final double AUTONOMOUS_MAX_ACCELERATION = 50;
			public static final double ROBOT_WIDTH = 29;
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
