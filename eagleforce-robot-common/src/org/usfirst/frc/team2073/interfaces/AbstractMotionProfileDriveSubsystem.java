package org.usfirst.frc.team2073.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team2073.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.util.MotionProfileGenerator;
import org.usfirst.frc.team2073.util.MotionProfileHelper;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AbstractMotionProfileDriveSubsystem extends AbstractSystemsControlDriveSubsystem {

	public AbstractMotionProfileDriveSubsystem() {
		initTalons();
		configSmartDashboard();
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
		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDistInInches) / (Drivetrain.WHEEL_DIAMETER * 5);
		double encoderTics = rotationDist*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		// TODO: check if high gear is enabled
		configuration.setEndDistance(encoderTics);
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
		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDist) / (Drivetrain.WHEEL_DIAMETER * 5);
		double encoderTics = rotationDist*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		configuration.setEndDistance(encoderTics);
		configuration.setIntervalVal(10);
		configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration.setVelocityOnly(false);
		return configuration;
	}

	/**
	 * List of profiles, first for outside then for inside.
	 */
	public ArrayList<MotionProfileConfiguration> straightIntoTurn(double linearDistanceInInches, double angleTurn) {
		MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
		MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();
		double outsideLinearDistance = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI) + linearDistanceInInches;
		double outsideRotations = (11.85 * Drivetrain.LOW_GEAR_RATIO * outsideLinearDistance)
				/ (Drivetrain.WHEEL_DIAMETER * 5);
		double insideRotations = (7.8 * Drivetrain.LOW_GEAR_RATIO * linearDistanceInInches)
				/ (Drivetrain.WHEEL_DIAMETER * 5);
		double outsiddeEncoderTics = outsideRotations*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		double insideEncoderTics = insideRotations*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
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
		double outsideRotations = (7.8 * Drivetrain.LOW_GEAR_RATIO * outsideLinearDistance)
				/ (Drivetrain.WHEEL_DIAMETER * 5);
		double insideRotations = (7.8 * Drivetrain.LOW_GEAR_RATIO * insideLinearDistance)
				/ (Drivetrain.WHEEL_DIAMETER * 5);
		double time = outsideRotations / Drivetrain.AUTONOMOUS_MAX_VELOCITY;
		double interiorVelocity = insideRotations / time;
		double outsiddeEncoderTics = outsideRotations*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
		double insideEncoderTics = insideRotations*Drivetrain.ENCODER_EDGES_PER_REVOLUTION;

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
		MotionProfileHelper.resetAndPushPoints(leftMotor, trajPointList, leftForwards);
		MotionProfileHelper.resetAndPushPoints(rightMotor, trajPointList, rightForwards);
		leftMotor.setSelectedSensorPosition(0, 0, 5);
		rightMotor.setSelectedSensorPosition(0, 0, 5);
		MotionProfileHelper.setF(leftMotor);
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
		resetMotionProfilingAndGeneratePoints(driveStraigtConfig(linearDistInInches), true, false);
	}

	public void autonPointTurn(double angle) {
		if (angle > 0)
			resetMotionProfilingAndGeneratePoints(pointTurnConfig(Math.abs(angle)), false, false);
		else
			resetMotionProfilingAndGeneratePoints(pointTurnConfig(Math.abs(angle)), true, true);

	}

	public void autonDriveBackward(double linearDistInInches) {
		resetMotionProfilingAndGeneratePoints(driveStraigtConfig(linearDistInInches), false, true);
	}

	public void autonStraightDriveIntoTurn(double linearDistanceInInches, double angleTurn) {
		List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
				.generatePoints(straightIntoTurn(linearDistanceInInches, angleTurn).get(0));
		List<TrajectoryPoint> insideTpList = MotionProfileGenerator
				.generatePoints(straightIntoTurn(linearDistanceInInches, angleTurn).get(1));

		if (angleTurn < 0) {
			MotionProfileHelper.resetAndPushPoints(leftMotor, outsideTpList, true);
			MotionProfileHelper.resetAndPushPoints(rightMotor, insideTpList, false);
		} else {
			MotionProfileHelper.resetAndPushPoints(rightMotor, outsideTpList, false);
			MotionProfileHelper.resetAndPushPoints(leftMotor, insideTpList, true);
		}
	}

	public void autonArcTurn(double angleTurn, double turnRadius, boolean isRightTurn) {
		List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
				.generatePoints(arcTurnConfiguration(angleTurn, turnRadius, isRightTurn).get(0));
		List<TrajectoryPoint> insideTpList = MotionProfileGenerator
				.generatePoints(arcTurnConfiguration(angleTurn, turnRadius, isRightTurn).get(1));
		if (isRightTurn) {
			MotionProfileHelper.resetAndPushPoints(leftMotor, outsideTpList, true);
			MotionProfileHelper.resetAndPushPoints(rightMotor, insideTpList, false);
		} else {
			MotionProfileHelper.resetAndPushPoints(rightMotor, outsideTpList, false);
			MotionProfileHelper.resetAndPushPoints(leftMotor, insideTpList, true);
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
