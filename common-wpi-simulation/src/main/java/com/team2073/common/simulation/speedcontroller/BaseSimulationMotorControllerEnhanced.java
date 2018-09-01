package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;

import static com.team2073.common.util.ExceptionUtil.throwUnsupported;

public abstract class BaseSimulationMotorControllerEnhanced extends BaseSimulationMotorController implements IMotorControllerEnhanced {

	@Override
	public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackSensor");
		return null;
	}

	@Override
	public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
		throwUnsupported("setStatusFramePeriod");
		return null;
	}

	@Override
	public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
		throwUnsupported("getStatusFramePeriod");
		return 0;
	}

	@Override
	public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
		throwUnsupported("configVelocityMeasurementPeriod");
		return null;
	}

	@Override
	public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
		throwUnsupported("configVelocityMeasurementWindow");
		return null;
	}

	@Override
	public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
		throwUnsupported("configForwardLimitSwitchSource");
		return null;
	}

	@Override
	public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
		throwUnsupported("configReverseLimitSwitchSource");
		return null;
	}

	@Override
	public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
		throwUnsupported("configPeakCurrentLimit");
		return null;
	}

	@Override
	public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
		throwUnsupported("configPeakCurrentDuration");
		return null;
	}

	@Override
	public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
		throwUnsupported("configContinuousCurrentLimit");
		return null;
	}

	@Override
	public void enableCurrentLimit(boolean enable) {
		throwUnsupported("enableCurrentLimit");
	}

}
