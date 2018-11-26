package com.team2073.common.util;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.*;

public class TalonUtil {
	private final static int TIMEOUT_MS = 100;

	private static class Configuration {
		private NeutralMode NEUTRAL_MODE = NeutralMode.Coast;
		// This is factory default.
		private double NEUTRAL_DEADBAND = 0.04;

		private boolean enableCurrentLimit = false;
		private boolean enableSoftLimit = false;
		private boolean enableLimitSwitch = false;
		private int forwardSoftLimit = 0;
		private int reverseSoftLimit = 0;

		private boolean inverted = false;
		private boolean sensorPhase = false;

		private int controlFramePeriodMs = 5;
		private int motionControlFramePeriodMs = 100;
		private int generalStatusFrameRateMs = 5;
		private int feedbackStatusFrameRateMs = 100;
		private int quadEncoderStatusFrameRateMs = 100;
		private int analogTempVbatStatusFrameRateMs = 100;
		private int pulseWidthStatusFrameRateMs = 100;
		private int basePidfStatusFrameRateMs = 100;

		private VelocityMeasPeriod velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
		private int velocityMeasurementRollingAverageWindow = 64;

		private double openLoopRampRate = 0.0;
		private double closedLoopRampRate = 0.0;
	}

	private static final Configuration DEFAULT_CONFIGURATION = new Configuration();
	private static final Configuration SLAVE_CONFIGURATION = new Configuration();
	private static final Configuration SENSOR_CONFIGURATION = new Configuration();

	/**
	 * Should Match how the controller is being used, default is factory default settings and is the base configuration.
	 */
	public enum ConfigurationType{
		DEFAULT,
		SLAVE,
		SENSOR
	}

	static {
		// This control frame value seems to need to be something reasonable to avoid the Talon's
		// LEDs behaving erratically.  Potentially try to increase as much as possible.
		SLAVE_CONFIGURATION.controlFramePeriodMs = 100;
		SLAVE_CONFIGURATION.motionControlFramePeriodMs = 1000;
		SLAVE_CONFIGURATION.generalStatusFrameRateMs = 1000;
		SLAVE_CONFIGURATION.feedbackStatusFrameRateMs = 1000;
		SLAVE_CONFIGURATION.quadEncoderStatusFrameRateMs = 1000;
		SLAVE_CONFIGURATION.analogTempVbatStatusFrameRateMs = 1000;
		SLAVE_CONFIGURATION.pulseWidthStatusFrameRateMs = 1000;



		SENSOR_CONFIGURATION.quadEncoderStatusFrameRateMs = 10;
		SENSOR_CONFIGURATION.feedbackStatusFrameRateMs = 10;
	}

	/**
	 * Resets talon to default settings and adjusts settings from there based on the ConfigurationType
	 */
	public static void resetTalon(IMotorControllerEnhanced talon, ConfigurationType configType) {
		Configuration config;
		switch (configType) {
			case SENSOR:
				config = SENSOR_CONFIGURATION;
				break;
			case SLAVE:
				config = SLAVE_CONFIGURATION;
				break;
			case DEFAULT:
				config = DEFAULT_CONFIGURATION;
				break;
			default:
				config = DEFAULT_CONFIGURATION;
		}
		talon.set(ControlMode.PercentOutput, 0.0);

		talon.changeMotionControlFramePeriod(config.motionControlFramePeriodMs);
		talon.clearMotionProfileHasUnderrun(TIMEOUT_MS);
		talon.clearMotionProfileTrajectories();

		talon.clearStickyFaults(TIMEOUT_MS);

		talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TIMEOUT_MS);
		talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TIMEOUT_MS);
		talon.overrideLimitSwitchesEnable(config.enableLimitSwitch);

		// Turn off re-zeroing by default.
		talon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, TIMEOUT_MS);
		talon.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, TIMEOUT_MS);

		talon.configNominalOutputForward(0, TIMEOUT_MS);
		talon.configNominalOutputReverse(0, TIMEOUT_MS);
		talon.configNeutralDeadband(config.NEUTRAL_DEADBAND, TIMEOUT_MS);

		talon.configPeakOutputForward(1.0, TIMEOUT_MS);
		talon.configPeakOutputReverse(-1.0, TIMEOUT_MS);

		talon.setNeutralMode(config.NEUTRAL_MODE);

		talon.configForwardSoftLimitThreshold(config.forwardSoftLimit, TIMEOUT_MS);
		talon.configForwardSoftLimitEnable(config.enableSoftLimit, TIMEOUT_MS);

		talon.configReverseSoftLimitThreshold(config.reverseSoftLimit, TIMEOUT_MS);
		talon.configReverseSoftLimitEnable(config.enableSoftLimit, TIMEOUT_MS);
		talon.overrideSoftLimitsEnable(config.enableSoftLimit);

		talon.setInverted(config.inverted);
		talon.setSensorPhase(config.sensorPhase);

		talon.selectProfileSlot(0, 0);

		talon.configVelocityMeasurementPeriod(config.velocityMeasurementPeriod, TIMEOUT_MS);
		talon.configVelocityMeasurementWindow(config.velocityMeasurementRollingAverageWindow, TIMEOUT_MS);

		talon.configOpenloopRamp(config.openLoopRampRate, TIMEOUT_MS);
		talon.configClosedloopRamp(config.closedLoopRampRate, TIMEOUT_MS);

		talon.configVoltageCompSaturation(0.0, TIMEOUT_MS);
		talon.configVoltageMeasurementFilter(32, TIMEOUT_MS);
		talon.enableVoltageCompensation(false);

		talon.enableCurrentLimit(config.enableCurrentLimit);

		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, config.generalStatusFrameRateMs, TIMEOUT_MS);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, config.feedbackStatusFrameRateMs, TIMEOUT_MS);

		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.quadEncoderStatusFrameRateMs, TIMEOUT_MS);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, config.analogTempVbatStatusFrameRateMs, TIMEOUT_MS);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, config.pulseWidthStatusFrameRateMs, TIMEOUT_MS);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, config.basePidfStatusFrameRateMs, TIMEOUT_MS);

		talon.setControlFramePeriod(ControlFrame.Control_3_General, config.controlFramePeriodMs);

	}

	/**
	 * Resets victor to default settings and adjusts settings from there based on the ConfigurationType
	 * Should usually be Slave mode for those being used with a talon, or default for open loop controls.
	 */
	public static void resetVictor(IMotorController victor, ConfigurationType configType) {
		Configuration config;
		switch (configType) {
			case SENSOR:
				config = SENSOR_CONFIGURATION;
				break;
			case SLAVE:
				config = SLAVE_CONFIGURATION;
				break;
			case DEFAULT:
				config = DEFAULT_CONFIGURATION;
				break;
			default:
				config = DEFAULT_CONFIGURATION;
		}
		victor.set(ControlMode.PercentOutput, 0.0);

		victor.changeMotionControlFramePeriod(config.motionControlFramePeriodMs);
		victor.clearMotionProfileHasUnderrun(TIMEOUT_MS);
		victor.clearMotionProfileTrajectories();

		victor.clearStickyFaults(TIMEOUT_MS);

		victor.overrideLimitSwitchesEnable(config.enableLimitSwitch);

		// Turn off re-zeroing by default.
		victor.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, TIMEOUT_MS);
		victor.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, TIMEOUT_MS);

		victor.configNominalOutputForward(0, TIMEOUT_MS);
		victor.configNominalOutputReverse(0, TIMEOUT_MS);
		victor.configNeutralDeadband(config.NEUTRAL_DEADBAND, TIMEOUT_MS);

		victor.configPeakOutputForward(1.0, TIMEOUT_MS);
		victor.configPeakOutputReverse(-1.0, TIMEOUT_MS);

		victor.setNeutralMode(config.NEUTRAL_MODE);

		victor.configForwardSoftLimitThreshold(config.forwardSoftLimit, TIMEOUT_MS);
		victor.configForwardSoftLimitEnable(config.enableSoftLimit, TIMEOUT_MS);

		victor.configReverseSoftLimitThreshold(config.reverseSoftLimit, TIMEOUT_MS);
		victor.configReverseSoftLimitEnable(config.enableSoftLimit, TIMEOUT_MS);
		victor.overrideSoftLimitsEnable(config.enableSoftLimit);

		victor.setInverted(config.inverted);
		victor.setSensorPhase(config.sensorPhase);

		victor.selectProfileSlot(0, 0);


		victor.configOpenloopRamp(config.openLoopRampRate, TIMEOUT_MS);
		victor.configClosedloopRamp(config.closedLoopRampRate, TIMEOUT_MS);

		victor.configVoltageCompSaturation(0.0, TIMEOUT_MS);
		victor.configVoltageMeasurementFilter(32, TIMEOUT_MS);
		victor.enableVoltageCompensation(false);

		victor.setStatusFramePeriod(StatusFrame.Status_1_General, config.generalStatusFrameRateMs, TIMEOUT_MS);
		victor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, config.feedbackStatusFrameRateMs, TIMEOUT_MS);

		victor.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, config.analogTempVbatStatusFrameRateMs, TIMEOUT_MS);
		victor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, config.basePidfStatusFrameRateMs, TIMEOUT_MS);

		victor.setControlFramePeriod(ControlFrame.Control_3_General, config.controlFramePeriodMs);

	}
}
