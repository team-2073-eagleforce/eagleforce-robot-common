package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.*;

import static com.team2073.common.util.ExceptionUtil.throwUnsupported;

public abstract class BaseSimulationMotorController implements IMotorController {
	@Override
	public void set(ControlMode Mode, double demand0, double demand1) {
		throwUnsupported("set(Mode, demand0, demand1)");
	}

	@Override
	public void neutralOutput() {
		throwUnsupported("neutralOutput");
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		throwUnsupported("setNeutralMode");
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		throwUnsupported("setSensorPhase");
	}

	@Override
	public boolean getInverted() {
		throwUnsupported("getInverted");
		return false;
	}

	@Override
	public void setInverted(boolean invert) {
		throwUnsupported("setInverted");
	}

	@Override
	public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throwUnsupported("configOpenloopRamp");
		return null;
	}

	@Override
	public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throwUnsupported("configClosedloopRamp");
		return null;
	}

	@Override
	public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
		throwUnsupported("configNominalOutputForward");
		return null;
	}

	@Override
	public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
		throwUnsupported("configNominalOutputReverse");
		return null;
	}

	@Override
	public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
		throwUnsupported("configNeutralDeadband");
		return null;
	}

	@Override
	public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
		throwUnsupported("configVoltageCompSaturation");
		return null;
	}

	@Override
	public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
		throwUnsupported("configVoltageMeasurementFilter");
		return null;
	}

	@Override
	public void enableVoltageCompensation(boolean enable) {
		throwUnsupported("enableVoltageCompensation");
	}

	@Override
	public double getBusVoltage() {
		throwUnsupported("getBusVoltage");
		return 0;
	}

	@Override
	public double getOutputCurrent() {
		throwUnsupported("getOutputCurrent");
		return 0;
	}

	@Override
	public double getTemperature() {
		throwUnsupported("getTemperature");
		return 0;
	}

	@Override
	public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackSensor");
		return null;
	}

	@Override
	public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackSensor");
		return null;
	}

	@Override
	public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
		throwUnsupported("configSensorTerm");
		return null;
	}

	@Override
	public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {
		throwUnsupported("setSelectedSensorPosition");
		return null;
	}

	@Override
	public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
		throwUnsupported("setControlFramePeriod");
		return null;
	}

	@Override
	public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
		throwUnsupported("setStatusFramePeriod");
		return null;
	}

	@Override
	public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
		throwUnsupported("getStatusFramePeriod");
		return 0;
	}

	@Override
	public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int deviceID, int timeoutMs) {
		throwUnsupported("configForwardLimitSwitchSource");
		return null;
	}

	@Override
	public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int deviceID, int timeoutMs) {
		throwUnsupported("configReverseLimitSwitchSource");
		return null;
	}

	@Override
	public void overrideLimitSwitchesEnable(boolean enable) {
		throwUnsupported("overrideLimitSwitchesEnable");

	}

	@Override
	public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {
		throwUnsupported("configForwardSoftLimitThreshhold");
		return null;
	}

	@Override
	public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {
		throwUnsupported("configReverseSoftLimitThreshold");
		return null;
	}

	@Override
	public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
		throwUnsupported("configForwardSoftLimitEnable");
		return null;
	}

	@Override
	public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
		throwUnsupported("configReverseSoftLimitEnable");
		return null;
	}

	@Override
	public void overrideSoftLimitsEnable(boolean enable) {
		throwUnsupported("overrideSoftLimitsEnable");
	}

	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kP");
		return null;
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kI");
		return null;
	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kD");
		return null;
	}

	@Override
	public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kF");
		return null;
	}

	@Override
	public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {
		throwUnsupported("config_IntegralZone");
		return null;
	}

	@Override
	public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
		throwUnsupported("configAllowableClosedloopError");
		return null;
	}

	@Override
	public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
		throwUnsupported("configMaxIntegralAccumulator");
		return null;
	}


	@Override
	public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
		throwUnsupported("setIntegralAccumulator");
		return null;
	}

	@Override
	public int getClosedLoopError(int pidIdx) {
		throwUnsupported("getClosedLoopError");
		return 0;
	}

	@Override
	public double getIntegralAccumulator(int pidIdx) {
		throwUnsupported("getIntegralAccumulator");
		return 0;
	}

	@Override
	public double getErrorDerivative(int pidIdx) {
		throwUnsupported("getErrorDerivative");
		return 0;
	}

	@Override
	public void selectProfileSlot(int slotIdx, int pidIdx) {
		throwUnsupported("selectProfileSlot");
	}

	@Override
	public int getSelectedSensorPosition(int pidIdx) {
		throwUnsupported("getSelectedSensorPosition");
		return 0;
	}

	@Override
	public int getSelectedSensorVelocity(int pidIdx) {
		throwUnsupported("getSelectedSensorVelocity");
		return 0;
	}

	@Override
	public int getActiveTrajectoryPosition() {
		throwUnsupported("getActiveTrajectoryPosition");
		return 0;
	}

	@Override
	public int getActiveTrajectoryVelocity() {
		throwUnsupported("getActiveTrajectoryVelocity");
		return 0;
	}

	@Override
	public double getActiveTrajectoryHeading() {
		throwUnsupported("getActiveTrajectoryHeading");
		return 0;
	}

	@Override
	public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {
		throwUnsupported("configMotionCruiseVelocity");
		return null;
	}

	@Override
	public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {
		throwUnsupported("configMotionAcceleration");
		return null;
	}

	@Override
	public ErrorCode clearMotionProfileTrajectories() {
		throwUnsupported("clearMotionProfileTrajectories");
		return null;
	}

	@Override
	public int getMotionProfileTopLevelBufferCount() {
		throwUnsupported("getMotionProfileTopLevelBufferCount");
		return 0;
	}

	@Override
	public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
		throwUnsupported("pushMotionProfileTrajectory");
		return null;
	}

	@Override
	public boolean isMotionProfileTopLevelBufferFull() {
		throwUnsupported("isMotionProfileTopLevelBufferFull");
		return false;
	}

	@Override
	public void processMotionProfileBuffer() {
		throwUnsupported("processMotionProfileBuffer");
	}

	@Override
	public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
		throwUnsupported("getMotionProfileStatus");
		return null;
	}

	@Override
	public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
		throwUnsupported("clearMotionProfileHasUnderrun");
		return null;
	}

	@Override
	public ErrorCode changeMotionControlFramePeriod(int periodMs) {
		throwUnsupported("changeMotionControlFramePeriod");
		return null;
	}

	@Override
	public ErrorCode getLastError() {
		throwUnsupported("getLastError");
		return null;
	}

	@Override
	public ErrorCode getFaults(Faults toFill) {
		throwUnsupported("getFaults");
		return null;
	}

	@Override
	public ErrorCode getStickyFaults(StickyFaults toFill) {
		throwUnsupported("getStickyFaults");
		return null;
	}

	@Override
	public ErrorCode clearStickyFaults(int timeoutMs) {
		throwUnsupported("clearStickyFaults");
		return null;
	}

	@Override
	public int getFirmwareVersion() {
		throwUnsupported("getFirmwareVersion");
		return 0;
	}

	@Override
	public boolean hasResetOccurred() {
		throwUnsupported("hasResetOccurred");
		return false;
	}

	@Override
	public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
		throwUnsupported("configSetCustomParam");
		return null;
	}

	@Override
	public int configGetCustomParam(int paramIndex, int timoutMs) {
		throwUnsupported("configGetCustomParam");
		return 0;
	}

	@Override
	public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
		throwUnsupported("configSetParameter");
		return null;
	}

	@Override
	public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
		throwUnsupported("configSetParameter");
		return null;
	}

	@Override
	public double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {
		throwUnsupported("configGetParameter");
		return 0;
	}

	@Override
	public double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {
		throwUnsupported("configGetParameter");
		return 0;
	}

	@Override
	public int getBaseID() {
		throwUnsupported("getBaseID");
		return 0;
	}

	@Override
	public int getDeviceID() {
		throwUnsupported("getDeviceID");
		return 0;
	}

	@Override
	public void follow(IMotorController masterToFollow) {
		throwUnsupported("follow");
	}

	@Override
	public void valueUpdated() {
		throwUnsupported("valueUpdated");
	}

}
