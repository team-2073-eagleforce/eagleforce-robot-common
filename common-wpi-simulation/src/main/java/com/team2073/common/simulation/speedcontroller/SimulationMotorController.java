package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StickyFaults;


public interface SimulationMotorController extends IMotorController {
	
	default void throwUnsupported(String methodName) {
		throw new UnsupportedOperationException("The class [" + this.getClass().getSimpleName() + "] must override [" + methodName + "] method to use this method.");
	}

	@Override
	default void set(ControlMode Mode, double demand) {
		throwUnsupported("set(ControlMode Mode, double demand)");
	}
	
	@Override
	default void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {
		throwUnsupported("set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1)");
	}
	
	@Override
	default void neutralOutput() {
		throwUnsupported("neutralOutput");
	}

	@Override
	default void setNeutralMode(NeutralMode neutralMode) {
		throwUnsupported("setNeutralMode");
	}

	@Override
	default void setSensorPhase(boolean PhaseSensor) {
		throwUnsupported("setSensorPhase");
	}

	@Override
	default boolean getInverted() {
		throwUnsupported("getInverted");
		return false;
	}

	@Override
	default void setInverted(boolean invert) {
		throwUnsupported("setInverted");
	}

	@Override
	default ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throwUnsupported("configOpenloopRamp");
		return null;
	}

	@Override
	default ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throwUnsupported("configClosedloopRamp");
		return null;
	}

	@Override
	default ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
		throwUnsupported("configNominalOutputForward");
		return null;
	}

	@Override
	default ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
		throwUnsupported("configNominalOutputReverse");
		return null;
	}

	@Override
	default ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
		throwUnsupported("configNeutralDeadband");
		return null;
	}

	@Override
	default ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
		throwUnsupported("configVoltageCompSaturation");
		return null;
	}

	@Override
	default ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
		throwUnsupported("configVoltageMeasurementFilter");
		return null;
	}

	@Override
	default void enableVoltageCompensation(boolean enable) {
		throwUnsupported("enableVoltageCompensation");
	}

	@Override
	default double getBusVoltage() {
		throwUnsupported("getBusVoltage");
		return 0;
	}

	@Override
	default double getTemperature() {
		throwUnsupported("getTemperature");
		return 0;
	}

	@Override
	default ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackSensor");
		return null;
	}

	@Override
	default ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackSensor");
		return null;
	}

	@Override
	default ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
		throwUnsupported("configSensorTerm");
		return null;
	}

	@Override
	default ErrorCode setSelectedSensorPosition(double sensorPos, int pidIdx, int timeoutMs) {
		throwUnsupported("setSelectedSensorPosition");
		return null;
	}

	@Override
	default ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
		throwUnsupported("setControlFramePeriod");
		return null;
	}

	@Override
	default ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
		throwUnsupported("setStatusFramePeriod");
		return null;
	}

	@Override
	default int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
		throwUnsupported("getStatusFramePeriod");
		return 0;
	}

	@Override
	default ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int deviceID, int timeoutMs) {
		throwUnsupported("configForwardLimitSwitchSource");
		return null;
	}

	@Override
	default ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int deviceID, int timeoutMs) {
		throwUnsupported("configReverseLimitSwitchSource");
		return null;
	}

	@Override
	default void overrideLimitSwitchesEnable(boolean enable) {
		throwUnsupported("overrideLimitSwitchesEnable");

	}

	@Override
	default ErrorCode configForwardSoftLimitThreshold(double forwardSensorLimit, int timeoutMs) {
		throwUnsupported("configForwardSoftLimitThreshhold");
		return null;
	}

	@Override
	default ErrorCode configReverseSoftLimitThreshold(double reverseSensorLimit, int timeoutMs) {
		throwUnsupported("configReverseSoftLimitThreshold");
		return null;
	}

	@Override
	default ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
		throwUnsupported("configForwardSoftLimitEnable");
		return null;
	}

	@Override
	default ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
		throwUnsupported("configReverseSoftLimitEnable");
		return null;
	}

	@Override
	default void overrideSoftLimitsEnable(boolean enable) {
		throwUnsupported("overrideSoftLimitsEnable");
	}

	@Override
	default ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kP");
		return null;
	}

	@Override
	default ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kI");
		return null;
	}

	@Override
	default ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kD");
		return null;
	}

	@Override
	default ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		throwUnsupported("config_kF");
		return null;
	}

	@Override
	default ErrorCode config_IntegralZone(int slotIdx, double izone, int timeoutMs) {
		throwUnsupported("config_IntegralZone");
		return null;
	}

	@Override
	default ErrorCode configAllowableClosedloopError(int slotIdx, double allowableCloseLoopError, int timeoutMs) {
		throwUnsupported("configAllowableClosedloopError");
		return null;
	}

	@Override
	default ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
		throwUnsupported("configMaxIntegralAccumulator");
		return null;
	}


	@Override
	default ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
		throwUnsupported("setIntegralAccumulator");
		return null;
	}

	@Override
	default double getClosedLoopError(int pidIdx) {
		throwUnsupported("getClosedLoopError");
		return 0;
	}

	@Override
	default double getIntegralAccumulator(int pidIdx) {
		throwUnsupported("getIntegralAccumulator");
		return 0;
	}

	@Override
	default double getErrorDerivative(int pidIdx) {
		throwUnsupported("getErrorDerivative");
		return 0;
	}

	@Override
	default void selectProfileSlot(int slotIdx, int pidIdx) {
		throwUnsupported("selectProfileSlot");
	}

	@Override
	default double getSelectedSensorPosition(int pidIdx) {
		throwUnsupported("getSelectedSensorPosition");
		return 0;
	}

	@Override
	default double getSelectedSensorVelocity(int pidIdx) {
		throwUnsupported("getSelectedSensorVelocity");
		return 0;
	}

	@Override
	default double getActiveTrajectoryPosition() {
		throwUnsupported("getActiveTrajectoryPosition");
		return 0;
	}

	@Override
	default double getActiveTrajectoryVelocity() {
		throwUnsupported("getActiveTrajectoryVelocity");
		return 0;
	}

//	@Override
//	default double getActiveTrajectoryHeading() {
//		throwUnsupported("getActiveTrajectoryHeading");
//		return 0;
//	}

	@Override
	default ErrorCode configMotionCruiseVelocity(double sensorUnitsPer100ms, int timeoutMs) {
		throwUnsupported("configMotionCruiseVelocity");
		return null;
	}

	@Override
	default ErrorCode configMotionAcceleration(double sensorUnitsPer100msPerSec, int timeoutMs) {
		throwUnsupported("configMotionAcceleration");
		return null;
	}

	@Override
	default ErrorCode clearMotionProfileTrajectories() {
		throwUnsupported("clearMotionProfileTrajectories");
		return null;
	}

	@Override
	default int getMotionProfileTopLevelBufferCount() {
		throwUnsupported("getMotionProfileTopLevelBufferCount");
		return 0;
	}

	@Override
	default ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
		throwUnsupported("pushMotionProfileTrajectory");
		return null;
	}

	@Override
	default boolean isMotionProfileTopLevelBufferFull() {
		throwUnsupported("isMotionProfileTopLevelBufferFull");
		return false;
	}

	@Override
	default void processMotionProfileBuffer() {
		throwUnsupported("processMotionProfileBuffer");
	}

	@Override
	default ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
		throwUnsupported("getMotionProfileStatus");
		return null;
	}

	@Override
	default ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
		throwUnsupported("clearMotionProfileHasUnderrun");
		return null;
	}

	@Override
	default ErrorCode changeMotionControlFramePeriod(int periodMs) {
		throwUnsupported("changeMotionControlFramePeriod");
		return null;
	}

	@Override
	default ErrorCode getLastError() {
		throwUnsupported("getLastError");
		return null;
	}

	@Override
	default ErrorCode getFaults(Faults toFill) {
		throwUnsupported("getFaults");
		return null;
	}

	@Override
	default ErrorCode getStickyFaults(StickyFaults toFill) {
		throwUnsupported("getStickyFaults");
		return null;
	}

	@Override
	default ErrorCode clearStickyFaults(int timeoutMs) {
		throwUnsupported("clearStickyFaults");
		return null;
	}

	@Override
	default int getFirmwareVersion() {
		throwUnsupported("getFirmwareVersion");
		return 0;
	}

	@Override
	default boolean hasResetOccurred() {
		throwUnsupported("hasResetOccurred");
		return false;
	}

	@Override
	default ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
		throwUnsupported("configSetCustomParam");
		return null;
	}

	@Override
	default int configGetCustomParam(int paramIndex, int timoutMs) {
		throwUnsupported("configGetCustomParam");
		return 0;
	}

	@Override
	default ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
		throwUnsupported("configSetParameter");
		return null;
	}

	@Override
	default ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
		throwUnsupported("configSetParameter");
		return null;
	}

	@Override
	default double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {
		throwUnsupported("configGetParameter");
		return 0;
	}

	@Override
	default double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {
		throwUnsupported("configGetParameter");
		return 0;
	}
	
	@Override
	default ErrorCode configSelectedFeedbackCoefficient(double coefficient, int pidIdx, int timeoutMs) {
		throwUnsupported("configSelectedFeedbackCoefficient");
		return null;
	}
	
	@Override
	default ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
		throwUnsupported("configClosedLoopPeakOutput");
		return null;
	}
	
	@Override
	default ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
		throwUnsupported("configClosedLoopPeriod");
		return null;
	}
	
	@Override
	default ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
		throwUnsupported("configAuxPIDPolarity");
		return null;
	}

	@Override
	default int getBaseID() {
		throwUnsupported("getBaseID");
		return 0;
	}

	@Override
	default int getDeviceID() {
		throwUnsupported("getDeviceID");
		return 0;
	}

	@Override
	default void follow(IMotorController masterToFollow) {
		throwUnsupported("follow");
	}

	@Override
	default void valueUpdated() {
		throwUnsupported("valueUpdated");
	}

	@Override
	default double getClosedLoopTarget(int pidIdx) {
		throwUnsupported("getClosedLoopTarget");
		return 0;
	}

	@Override
	default ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
		throwUnsupported("configMotionProfileTrajectoryPeriod");
		return null;
	}

	@Override
	default ControlMode getControlMode() {
		throwUnsupported("getControlMode");
		return null;
	}

}
