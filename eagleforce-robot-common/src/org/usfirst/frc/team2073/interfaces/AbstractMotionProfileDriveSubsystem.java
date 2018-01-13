package org.usfirst.frc.team2073.interfaces;

import java.util.List;

import org.usfirst.frc.team2073.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.util.MotionProfileGenerator;
import org.usfirst.frc.team2073.util.MotionProfileHelper;

import com.ctre.phoenix.motion.TrajectoryPoint;

public abstract class AbstractMotionProfileDriveSubsystem extends AbstractSystemsControlDriveSubsystem {

	public AbstractMotionProfileDriveSubsystem() {
		initTalons();
	}

	private void initTalons() {
		MotionProfileHelper.initTalon(leftMotor);
		MotionProfileHelper.initTalon(rightMotor);
	}

	public MotionProfileConfiguration driveStraigtConfig(double linearDistInInches) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDistInInches /* *(5/8) */)
				/ (Drivetrain.WHEEL_DIAMETER * 5);// TODO: check if high gear is enabled
		configuration.setEndDistance(rotationDist);
		configuration.setInterval(10);
		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration.setVelocityOnly(false);
		return configuration;
	}

	public MotionProfileConfiguration pointTurnConfig(double angleTurn) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double linearDist = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI);
		double rotationDist = (8 * Drivetrain.LOW_GEAR_RATIO * linearDist) / (Drivetrain.WHEEL_DIAMETER * 5);
		configuration.setEndDistance(rotationDist);
		configuration.setInterval(10);
		configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY);
		configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
		configuration.setVelocityOnly(false);
		return configuration;
	}

	public void resetMotionProfiling(MotionProfileConfiguration config, boolean leftForwards, boolean rightForwards) {
		List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
		MotionProfileHelper.resetAndPushPoints(leftMotor, trajPointList, leftForwards);
		MotionProfileHelper.resetAndPushPoints(rightMotor, trajPointList, rightForwards);
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
		// TODO: decide whether to check if both are finished or if at least one is
		// finished
		return MotionProfileHelper.isFinished(leftMotor) && MotionProfileHelper.isFinished(rightMotor);
	}

	public void autonDriveForward(double linearDistInInches) {
		resetMotionProfiling(driveStraigtConfig(linearDistInInches), true, false);
	}

	public void autonPointTurn(double angle) {
		if (angle > 0)
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), false, false);
		else
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), true, true);

	}

	public void autonDriveBackward(double linearDistInInches) {
		resetMotionProfiling(driveStraigtConfig(linearDistInInches), false, true);
	}
}
