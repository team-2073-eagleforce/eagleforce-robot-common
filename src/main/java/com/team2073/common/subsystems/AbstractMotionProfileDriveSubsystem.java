package org.usfirst.frc.team2073.robot.subsystems;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team2073.robot.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.robot.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.robot.util.MotionProfileGenerator;
import org.usfirst.frc.team2073.robot.util.MotionProfileHelper;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AbstractMotionProfileDriveSubsystem extends AbstractSystemsControlDriveSubsystem {

	public AbstractMotionProfileDriveSubsystem() {
		initTalons();
		configSmartDashboard();
		
		leftMotor.setInverted(Defaults.LEFT_MOTOR_DEFAULT_DIRECTION);
		leftMotorSlave.setInverted(Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		rightMotor.setInverted(Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION);
		rightMotorSlave.setInverted(Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
	}

	private void initTalons() {
		MotionProfileHelper.initTalon(leftMotor);
		MotionProfileHelper.initTalon(rightMotor);
	}

	private void configSmartDashboard() {
		SmartDashboard.putNumber(DashboardKeys.LEFT_DRIVE_F_GAIN, Defaults.LEFT_DRIVE_F_GAIN);
		SmartDashboard.putNumber(DashboardKeys.RIGHT_DRIVE_F_GAIN, Defaults.RIGHT_DRIVE_F_GAIN);

	}

	public MotionProfileConfiguration driveStraigtConfig(double linearDistInInches) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double rotationDist = (linearDistInInches) / (Drivetrain.WHEEL_CIRCUMFERENCE);
		// TODO: check if high gear is enabled
		double encoderRevolutions = rotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		configuration.setEndDistance(encoderRevolutions);
		configuration.setIntervalVal(10);
		configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration.setVelocityOnly(false);
		return configuration;
	}
	
	public MotionProfileConfiguration pointTurnConfig(double angleTurn) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double linearDist = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI);
		double rotationDist = (Drivetrain.LOW_GEAR_RATIO * linearDist) / (Drivetrain.WHEEL_CIRCUMFERENCE);
		configuration.setEndDistance(rotationDist);
		configuration.setIntervalVal(10);
		configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration.setVelocityOnly(false);
		return configuration;
	}
	
	public void resetMotionProfiling(MotionProfileConfiguration config, boolean leftForwards, boolean rightForwards) {
		List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
		MotionProfileHelper.resetTalon(leftMotor);
		MotionProfileHelper.resetTalon(rightMotor);
		MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, trajPointList, leftForwards,
				Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, trajPointList, rightForwards,
				Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		leftMotor.setSelectedSensorPosition(0, 0, 5);
		rightMotor.setSelectedSensorPosition(0, 0, 5);
		MotionProfileHelper.setF(leftMotor);
		// MotionProfileHelper.setFRightSide(rightMotor);
		MotionProfileHelper.setF(rightMotor);
	}

	public void processMotionProfiling() {
		MotionProfileHelper.processPoints(leftMotor);
		MotionProfileHelper.processPoints(rightMotor);
	}

	public void stopMotionProfiling() {
		MotionProfileHelper.stopTalon(leftMotor);
		MotionProfileHelper.stopTalon(rightMotor);
	}

	public boolean isMotionProfilingFinished() {
		return MotionProfileHelper.isFinished(leftMotor) && MotionProfileHelper.isFinished(rightMotor);
	}

	public void autonDriveForward(double linearDistInInches) {
		resetMotionProfiling(driveStraigtConfig(linearDistInInches), true, false);
	}

	public void autonPointTurn(double angle) {
		if (angle > 0)
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), true, true);
		else
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), false, false);

	}

	public void autonDriveBackward(double linearDistInInches) {
		resetMotionProfiling(driveStraigtConfig(linearDistInInches), false, true);
	}


	/**
	 * List of profiles, first for outside then for inside.
	 */
	public ArrayList<MotionProfileConfiguration> straightIntoTurn(double linearDistanceInInches, double angleTurn) {
		MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
		MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();
		double outsideLinearDistance = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI) + linearDistanceInInches;
		double outsideRotations = outsideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
		double insideRotations = linearDistanceInInches / Drivetrain.WHEEL_CIRCUMFERENCE;
		double outsiddeEncoderTics = outsideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		double insideEncoderTics = insideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		configuration1.setEndDistance(outsiddeEncoderTics);
		configuration2.setEndDistance(insideEncoderTics);
		configuration1.setIntervalVal(10);
		configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration1.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration1.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration1.setVelocityOnly(false);
		configuration2.setIntervalVal(10);
		configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration2.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration2.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration2.setVelocityOnly(false);
		ArrayList<MotionProfileConfiguration> configList = new ArrayList<MotionProfileConfiguration>();
		configList.add(configuration1);
		configList.add(configuration2);
		return configList;
	}

	/**
	 * List of profiles, first for outside then for inside.
	 */
	public ArrayList<MotionProfileConfiguration> arcTurnConfiguration(double angleTurn, double turnRadius,
			boolean isRightTurn) {
		MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
		MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();

		double outsideLinearDistance = 2 * Math.PI * (turnRadius + Drivetrain.ROBOT_WIDTH) * (angleTurn / 360);
		double insideLinearDistance = 2 * Math.PI * turnRadius * (angleTurn / 360);
		double outsideRotations = outsideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
		double insideRotations = insideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
		double time = outsideRotations / Drivetrain.AUTONOMOUS_MAX_VELOCITY;
		double interiorVelocity = insideRotations / time;
		double outsiddeEncoderTics = outsideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		double insideEncoderTics = insideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;

		configuration1.setEndDistance(outsiddeEncoderTics);
		configuration1.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration2.setEndDistance(insideEncoderTics);
		configuration2.setMaxVel(interiorVelocity);

		configuration1.setForwards(true);
		configuration1.setIntervalVal(10);
		configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration1.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration1.setVelocityOnly(false);

		configuration2.setForwards(true);
		configuration2.setIntervalVal(10);
		configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration2.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration2.setVelocityOnly(false);

		ArrayList<MotionProfileConfiguration> configList = new ArrayList<MotionProfileConfiguration>();
		configList.add(configuration1);
		configList.add(configuration2);
		return configList;
	}

	public void resetMotionProfilingAndGeneratePoints(MotionProfileConfiguration config, boolean leftForwards,
			boolean rightForwards) {
		List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
		MotionProfileHelper.resetTalon(leftMotor);
		MotionProfileHelper.resetTalon(rightMotor);
		MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, trajPointList, leftForwards,
				Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, trajPointList, rightForwards,
				Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		leftMotor.setSelectedSensorPosition(0, 0, 5);
		rightMotor.setSelectedSensorPosition(0, 0, 5);
		MotionProfileHelper.setF(leftMotor);
		MotionProfileHelper.setF(rightMotor);
	}

	public void autonStraightDriveIntoTurn(double linearDistanceInInches, double angleTurn) {
		List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
				.generatePoints(straightIntoTurn(linearDistanceInInches, angleTurn).get(0));
		List<TrajectoryPoint> insideTpList = MotionProfileGenerator
				.generatePoints(straightIntoTurn(linearDistanceInInches, angleTurn).get(1));

		if (angleTurn < 0) {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, outsideTpList, true,
					Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, insideTpList, false,
					Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		} else {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, insideTpList, true,
					Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, outsideTpList, false,
					Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		}
	}

	public void autonArcTurn(double angleTurn, double turnRadius, boolean isRightTurn) {
		List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
				.generatePoints(arcTurnConfiguration(angleTurn, turnRadius, isRightTurn).get(0));
		List<TrajectoryPoint> insideTpList = MotionProfileGenerator
				.generatePoints(arcTurnConfiguration(angleTurn, turnRadius, isRightTurn).get(1));
		if (isRightTurn) {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, outsideTpList, true,
					Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, insideTpList, false,
					Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		} else {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, insideTpList, true,
					Defaults.LEFT_MOTOR_DEFAULT_DIRECTION, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, outsideTpList, false,
					Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
		}
	}

	public void changeFGain(TalonSRX motor, double value, String smartDashboardKey, double defaultF) {
		MotionProfileHelper.changeF(motor, value, smartDashboardKey, defaultF);
	}

	public void adjustF(double startingGryo) {
		if (getGyroAngle() < startingGryo - .2) {
			changeFGain(leftMotor, .01, DashboardKeys.LEFT_DRIVE_F_GAIN, Defaults.LEFT_DRIVE_F_GAIN);
			changeFGain(rightMotor, -.01, DashboardKeys.RIGHT_DRIVE_F_GAIN, Defaults.RIGHT_DRIVE_F_GAIN);
		} else if (getGyroAngle() > startingGryo + .2) {
			changeFGain(rightMotor, .01, DashboardKeys.RIGHT_DRIVE_F_GAIN, Defaults.RIGHT_DRIVE_F_GAIN);
			changeFGain(leftMotor, -.01, DashboardKeys.LEFT_DRIVE_F_GAIN, Defaults.LEFT_DRIVE_F_GAIN);
		}
	}

}
