package com.team2073.common.subsystems;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.domain.MotionProfileConfiguration;
import com.team2073.common.util.MotionProfileGenerator;
import com.team2073.common.util.MotionProfileHelper;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;

public abstract class AbstractMotionProfileDriveSubsystem extends AbstractSystemsControlDriveSubsystem {
	private final String leftDriveFgainDashboardKey;
	private final double defaultLeftDriveFgain;
	private final String rightDriveFgainDashboardKey;
	private final double defaultRightDriveFgain;
	
	private final boolean leftMotorDefaultDirection;
	private final boolean leftSlaveMotorDefaultDirection;
	private final boolean rightMotorDefaultDirection;
	private final boolean rightSlaveMotorDefaultDirection;
	
	private final double robotWidth;
	private final double wheelCircumference;
	private final double encoderEdgesPerRevolution;
	
	private final double autonomousMaxVelocity;
	private final double autonomousMaxAcceleration;
	
	private final double highGearRatio;
	private final double lowGearRatio;

	public AbstractMotionProfileDriveSubsystem(
			TalonSRX leftMotor, TalonSRX rightMotor,
			TalonSRX leftMotorSlave, TalonSRX rightMotorSlave,
			Solenoid solenoid1, Solenoid solenoid2,
			ADXRS450_Gyro gyro,
			String leftDriveFgainDashboardKey, double defaultLeftDriveFgain,
			String rightDriveFgainDashboardKey, double defaultRightDriveFgain,
			boolean leftMotorDefaultDirection, boolean leftSlaveMotorDefaultDirection,
			boolean rightMotorDefaultDirection, boolean rightSlaveMotorDefaultDirection,
			double robotWidth, double wheelCircumference, double encoderEdgesPerRevolution,
			double autonomousMaxVelocity, double autonomousMaxAcceleration,
			double highGearRatio, double lowGearRatio) {
		super(leftMotor, rightMotor, leftMotorSlave, rightMotorSlave, solenoid1, solenoid2, gyro);
		
		this.leftDriveFgainDashboardKey = leftDriveFgainDashboardKey;
		this.defaultLeftDriveFgain = defaultLeftDriveFgain;
		this.rightDriveFgainDashboardKey = rightDriveFgainDashboardKey;
		this.defaultRightDriveFgain = defaultRightDriveFgain;
		
		this.leftMotorDefaultDirection = leftMotorDefaultDirection;
		this.leftSlaveMotorDefaultDirection = leftSlaveMotorDefaultDirection;
		this.rightMotorDefaultDirection = rightMotorDefaultDirection;
		this.rightSlaveMotorDefaultDirection = rightSlaveMotorDefaultDirection;
		
		this.robotWidth = robotWidth;
		this.wheelCircumference = wheelCircumference;
		this.encoderEdgesPerRevolution = encoderEdgesPerRevolution;
		
		this.autonomousMaxVelocity = autonomousMaxVelocity;
		this.autonomousMaxAcceleration = autonomousMaxAcceleration;
		
		this.highGearRatio = highGearRatio;
		this.lowGearRatio = lowGearRatio;
		
		initTalons();
		
		leftMotor.setInverted(leftMotorDefaultDirection);
		leftMotorSlave.setInverted(leftSlaveMotorDefaultDirection);
		rightMotor.setInverted(rightMotorDefaultDirection);
		rightMotorSlave.setInverted(rightSlaveMotorDefaultDirection);
	}
	
	private void initTalons() {
		MotionProfileHelper.initTalon(leftMotor, leftDriveFgainDashboardKey, defaultLeftDriveFgain);
		MotionProfileHelper.initTalon(rightMotor, rightDriveFgainDashboardKey, defaultRightDriveFgain);
	}

	public MotionProfileConfiguration driveStraightConfig(double linearDistInInches) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double rotationDist = (linearDistInInches) / (wheelCircumference);
		// TODO: check if high gear is enabled
		double encoderRevolutions = rotationDist * encoderEdgesPerRevolution;
		configuration.setEndDistance(encoderRevolutions);
		configuration.setIntervalVal(10);
		configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration.setMaxVel(autonomousMaxVelocity);
		configuration.setMaxAcc(autonomousMaxAcceleration);
		configuration.setVelocityOnly(false);
		return configuration;
	}
	
	public MotionProfileConfiguration pointTurnConfig(double angleTurn) {
		MotionProfileConfiguration configuration = new MotionProfileConfiguration();
		double linearDist = (angleTurn / 360) * (robotWidth * Math.PI);
		double rotationDist = (lowGearRatio * linearDist) / (wheelCircumference);
		configuration.setEndDistance(rotationDist);
		configuration.setIntervalVal(10);
		configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration.setMaxVel(autonomousMaxVelocity);
		configuration.setMaxAcc(autonomousMaxAcceleration);
		configuration.setVelocityOnly(false);
		return configuration;
	}
	
	public void resetMotionProfiling(MotionProfileConfiguration config, boolean leftForwards, boolean rightForwards) {
		List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
		MotionProfileHelper.resetTalon(leftMotor);
		MotionProfileHelper.resetTalon(rightMotor);
		MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, trajPointList, leftForwards,
				leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
		MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, trajPointList, rightForwards,
				rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
		leftMotor.setSelectedSensorPosition(0, 0, 5);
		rightMotor.setSelectedSensorPosition(0, 0, 5);
		MotionProfileHelper.setF(leftMotor, leftDriveFgainDashboardKey, defaultLeftDriveFgain);
		// MotionProfileHelper.setFRightSide(rightMotor);
		MotionProfileHelper.setF(rightMotor, rightDriveFgainDashboardKey, defaultRightDriveFgain);
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
		resetMotionProfiling(driveStraightConfig(linearDistInInches), true, false);
	}

	public void autonPointTurn(double angle) {
		if (angle > 0)
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), true, true);
		else
			resetMotionProfiling(pointTurnConfig(Math.abs(angle)), false, false);

	}

	public void autonDriveBackward(double linearDistInInches) {
		resetMotionProfiling(driveStraightConfig(linearDistInInches), false, true);
	}


	/**
	 * List of profiles, first for outside then for inside.
	 */
	public ArrayList<MotionProfileConfiguration> straightIntoTurn(double linearDistanceInInches, double angleTurn) {
		MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
		MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();
		double outsideLinearDistance = (angleTurn / 360) * (robotWidth * Math.PI) + linearDistanceInInches;
		double outsideRotations = outsideLinearDistance / wheelCircumference;
		double insideRotations = linearDistanceInInches / wheelCircumference;
		double outsiddeEncoderTics = outsideRotations * encoderEdgesPerRevolution;
		double insideEncoderTics = insideRotations * encoderEdgesPerRevolution;
		configuration1.setEndDistance(outsiddeEncoderTics);
		configuration2.setEndDistance(insideEncoderTics);
		configuration1.setIntervalVal(10);
		configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration1.setMaxVel(autonomousMaxVelocity);
		configuration1.setMaxAcc(autonomousMaxAcceleration);
		configuration1.setVelocityOnly(false);
		configuration2.setIntervalVal(10);
		configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration2.setMaxVel(autonomousMaxVelocity);
		configuration2.setMaxAcc(autonomousMaxAcceleration);
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

		double outsideLinearDistance = 2 * Math.PI * (turnRadius + robotWidth) * (angleTurn / 360);
		double insideLinearDistance = 2 * Math.PI * turnRadius * (angleTurn / 360);
		double outsideRotations = outsideLinearDistance / wheelCircumference;
		double insideRotations = insideLinearDistance / wheelCircumference;
		double time = outsideRotations / autonomousMaxVelocity;
		double interiorVelocity = insideRotations / time;
		double outsiddeEncoderTics = outsideRotations * encoderEdgesPerRevolution;
		double insideEncoderTics = insideRotations * encoderEdgesPerRevolution;

		configuration1.setEndDistance(outsiddeEncoderTics);
		configuration1.setMaxVel(autonomousMaxVelocity);
		configuration2.setEndDistance(insideEncoderTics);
		configuration2.setMaxVel(interiorVelocity);

		configuration1.setForwards(true);
		configuration1.setIntervalVal(10);
		configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration1.setMaxAcc(autonomousMaxAcceleration);
		configuration1.setVelocityOnly(false);

		configuration2.setForwards(true);
		configuration2.setIntervalVal(10);
		configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
		configuration2.setMaxAcc(autonomousMaxAcceleration);
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
				leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
		MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, trajPointList, rightForwards,
				rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
		leftMotor.setSelectedSensorPosition(0, 0, 5);
		rightMotor.setSelectedSensorPosition(0, 0, 5);
		MotionProfileHelper.setF(leftMotor, leftDriveFgainDashboardKey, defaultLeftDriveFgain);
		MotionProfileHelper.setF(rightMotor, rightDriveFgainDashboardKey, defaultRightDriveFgain);
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
					leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, insideTpList, false,
					rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
		} else {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, insideTpList, true,
					leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, outsideTpList, false,
					rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
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
					leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, insideTpList, false,
					rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
		} else {
			MotionProfileHelper.resetTalon(leftMotor);
			MotionProfileHelper.resetTalon(rightMotor);
			MotionProfileHelper.pushPoints(leftMotor, leftMotorSlave, insideTpList, true,
					leftMotorDefaultDirection, leftSlaveMotorDefaultDirection);
			MotionProfileHelper.pushPoints(rightMotor, rightMotorSlave, outsideTpList, false,
					rightMotorDefaultDirection, rightSlaveMotorDefaultDirection);
		}
	}

	public void changeFGain(TalonSRX motor, double value, String smartDashboardKey, double defaultF) {
		MotionProfileHelper.changeF(motor, value, smartDashboardKey, defaultF);
	}

	public void adjustF(double startingGryo) {
		if (getGyroAngle() < startingGryo - .2) {
			changeFGain(leftMotor, .01, leftDriveFgainDashboardKey, defaultLeftDriveFgain);
			changeFGain(rightMotor, -.01, rightDriveFgainDashboardKey, defaultRightDriveFgain);
		} else if (getGyroAngle() > startingGryo + .2) {
			changeFGain(rightMotor, .01, rightDriveFgainDashboardKey, defaultRightDriveFgain);
			changeFGain(leftMotor, -.01, leftDriveFgainDashboardKey, defaultLeftDriveFgain);
		}
	}
}
