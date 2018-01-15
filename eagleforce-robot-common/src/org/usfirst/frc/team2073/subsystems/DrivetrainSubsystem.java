package org.usfirst.frc.team2073.subsystems;

import org.usfirst.frc.team2073.commands.drive.DriveWithJoystickCommand;
import org.usfirst.frc.team2073.interfaces.AbstractMotionProfileDriveSubsystem;

public class DrivetrainSubsystem extends AbstractMotionProfileDriveSubsystem{

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DriveWithJoystickCommand());
	}
//	
//	public static final double DEFAULT_INVERSE = .2;
//	public static final double DEFAULT_SENSE = .7;
//
//	private final TalonSRX leftMotor;
//	private final TalonSRX leftMotorSlave;
//	private final TalonSRX rightMotor;
//	private final TalonSRX rightMotorSlave;
//	private final Solenoid solenoid1;
//	private final Solenoid solenoid2;
//
//	private double preTurn;//TODO: is this needed?
//
//	public DrivetrainSubsystem() {
//		leftMotor = RobotMap.getLeftMotor();
//		leftMotorSlave = RobotMap.getLeftMotorSlave();
//		rightMotor = RobotMap.getRightMotor();
//		rightMotorSlave = RobotMap.getRightMotorSlave();
//		solenoid1 = RobotMap.getDriveSolenoid1();
//		solenoid2 = RobotMap.getDriveSolenoid2();
//
//		setSlaves();
//		shiftLowGear();
//		configEncoders();
//		initTalons();
//		enableBrakeMode();
//	}
//
//	@Override
//	protected void initDefaultCommand() {
//		setDefaultCommand(new DriveWithJoystickCommand());
//	}
//
//	private void setSlaves() {
//		TalonHelper.setFollowerOf(leftMotorSlave, leftMotor);
//		TalonHelper.setFollowerOf(rightMotorSlave, rightMotor);
//	}
//	
//	@Override
//	public void configEncoders() {
//		leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 5);
//		rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 5);
////		leftMotor.configEncoderCodesPerRev(1024);
////		rightMotor.configEncoderCodesPerRev(1024);
//	}
//	
//	private void initTalons() {
//		MotionProfileHelper.initTalon(leftMotor);
//		MotionProfileHelper.initTalon(rightMotor);
//	}
//
//	public MotionProfileConfiguration driveStraigtConfig(double linearDistInInches) {
//		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
//		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDistInInches /* *(5/8)*/) / (Drivetrain.WHEEL_DIAMETER * 5);//TODO: check if high gear is enabled
//		configuration.setEndDistance(rotationDist);
//		configuration.setInterval(10);
//		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
//		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
//		configuration.setVelocityOnly(false);
//		return configuration;
//	}
//
//	public MotionProfileConfiguration pointTurnConfig(double angleTurn) {
//		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
//		double linearDist = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI);
//		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDist) / (Drivetrain.WHEEL_DIAMETER * 5);
//		configuration.setEndDistance(rotationDist);
//		configuration.setInterval(10);
//		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
//		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
//		configuration.setVelocityOnly(false);
//		return configuration;
//	}
//
//	public double turnSense(double ptart) {
//		double sense = SmartDashboard.getNumber(DashboardKeys.SENSE, DEFAULT_SENSE);
//		return sense * ptart * ptart * ptart + ptart * (1 - sense);
//	}
//
//	public double inverse(double start) {
//		double inverse = SmartDashboard.getNumber(DashboardKeys.INVERSE, DEFAULT_INVERSE);
//		return (start - preTurn) * inverse + start;
//	}
//
//	public void pointTurn(double turn) {
//		rightMotor.set(ControlMode.PercentOutput, -turn);
//		leftMotor.set(ControlMode.PercentOutput, -turn);
//	}
//
//	public void move(double speed, double turn) {
//
//		double rightSide = -(inverse(speed) - (inverse(speed) * turnSense(turn)));
//		double leftSide = inverse(speed) + (inverse(speed) * turnSense(turn));
//
//		if (RobotMap.isBallIntakeForwards()) {
//			rightMotor.set(ControlMode.PercentOutput, rightSide);
//			leftMotor.set(ControlMode.PercentOutput, leftSide);
//		} else {
//			leftMotor.set(ControlMode.PercentOutput, rightSide);
//			rightMotor.set(ControlMode.PercentOutput, leftSide);
//		}
//	}
//
//	public void shiftHighGear() {
//		solenoid1.set(false);//TODO: rename misleading shiftHighGear/shiftLowGear names
//		solenoid2.set(true);
//	}
//
//	public void shiftLowGear() {
//		solenoid1.set(true);
//		solenoid2.set(false);
//	}
//
//	public void resetMotionProfiling(MotionProfileConfiguration config, boolean leftForwards, boolean rightForwards) {
//		List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
//		MotionProfileHelper.resetAndPushPoints(leftMotor, trajPointList, leftForwards);
//		MotionProfileHelper.resetAndPushPoints(rightMotor, trajPointList, rightForwards);
//		leftMotor.setSelectedSensorPosition(0, 0, 5);
//		rightMotor.setSelectedSensorPosition(0, 0, 5);
//		MotionProfileHelper.setF(leftMotor);
////		MotionProfileHelper.setFRightSide(rightMotor);
//		MotionProfileHelper.setF(rightMotor);
//	}
//
//	public void processMotionProfiling() {
//		MotionProfileHelper.processPoints(leftMotor);
//		MotionProfileHelper.processPoints(rightMotor);
//	}
//
//	public void stopMotionProfiling() {
//		MotionProfileHelper.stopTalon(leftMotor);
//		MotionProfileHelper.stopTalon(rightMotor);
//	}
//
//	public boolean isMotionProfilingFinished() {
//		// TODO: decide whether to check if both are finished or if at least one is
//		// finished
//		return MotionProfileHelper.isFinished(leftMotor) && MotionProfileHelper.isFinished(rightMotor);
//	}
//
//	public void autonDriveForward(double linearDistInInches) {
//		resetMotionProfiling(driveStraigtConfig(linearDistInInches), true, false);
//	}
//	
//	public void autonPointTurn(double angle) {
//		if(angle > 0)
//			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), false, false);
//		else
//			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), true, true);
//			
//	}
//	
//	public void autonDriveBackward(double linearDistInInches) {
//		resetMotionProfiling(driveStraigtConfig(linearDistInInches), false, true);
//	}
//	
//	@Override
//	public void stopBrakeMode() {
//		leftMotor.setNeutralMode(NeutralMode.Coast);
//		rightMotor.setNeutralMode(NeutralMode.Coast);
//	}
//	
//	@Override
//	public void enableBrakeMode() {
//		leftMotor.setNeutralMode(NeutralMode.Brake);
//		rightMotor.setNeutralMode(NeutralMode.Brake);
//		}
//
//	
}
