package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

public interface SimulationMotorControllerEnhanced extends SimulationMotorController, IMotorControllerEnhanced {

    @Override
    default ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
//      NO OP
        return null;
    }

    @Override
    default ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
        throwUnsupported("setStatusFramePeriod");
        return null;
    }

    @Override
    default int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
        throwUnsupported("getStatusFramePeriod");
        return 0;
    }
    
    @Override
    default SensorCollection getSensorCollection() {
        throwUnsupported("SensorCollection");
        return null;
    }

    @Override
    default ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
        throwUnsupported("configVelocityMeasurementPeriod");
        return null;
    }

    @Override
    default ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
        throwUnsupported("configVelocityMeasurementWindow");
        return null;
    }

    @Override
    default ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
        throwUnsupported("configForwardLimitSwitchSource");
        return null;
    }

    @Override
    default ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
        throwUnsupported("configReverseLimitSwitchSource");
        return null;
    }

    @Override
    default ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
        throwUnsupported("configPeakCurrentLimit");
        return null;
    }

    @Override
    default ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
        throwUnsupported("configPeakCurrentDuration");
        return null;
    }

    @Override
    default ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
        throwUnsupported("configContinuousCurrentLimit");
        return null;
    }

    @Override
    default void enableCurrentLimit(boolean enable) {
        throwUnsupported("enableCurrentLimit");
    }

	@Override
	default double getOutputCurrent() {
		throwUnsupported("getOutputCurrent");
		return 0;
	}

}
