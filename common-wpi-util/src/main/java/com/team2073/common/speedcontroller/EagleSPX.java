package com.team2073.common.speedcontroller;

import com.team2073.common.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
//import com.google.inject.Inject;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRunner;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EagleSPX extends VictorSPX implements SmartDashboardAware {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String name;
	private boolean isTestingEnabled = false;
	private double safePercentage = .5;
	private Timer timer = new Timer();

	private double lastPercentOutForward = 0;
	private double lastPercentOutReverse = 0;
	private double lastkPSlot0 = 0;
	private double lastkISlot0 = 0;
	private double lastkDSlot0 = 0;
	private double lastkFSlot0 = 0;

	private double lastkPSlot1 = 0;
	private double lastkISlot1 = 0;
	private double lastkDSlot1 = 0;
	private double lastkFSlot1 = 0;
	private boolean enabledTestingMode = false;

	/**
	 * 
	 * @param deviceNumber
	 * @param name
	 * @param safePercentage
	 *            safePercentage is the percent of the otherwise set speed, from 0
	 *            to 1
	 */
	public EagleSPX(int deviceNumber, String name, double safePercentage) {
		super(deviceNumber);
		this.name = name;
		this.safePercentage = safePercentage;

	}

//	@Inject
	public void registerSmartDashboardAware(SmartDashboardAwareRunner smartDashboardAwareRunner) {
		smartDashboardAwareRunner.registerInstance(this);
	}

	@Override
	public void updateSmartDashboard() {
		SmartDashboard.setDefaultBoolean(name + " Enable test Mode", false);
		if (logger.isTraceEnabled()) {
			SmartDashboard.putNumber(name, this.getMotorOutputVoltage());
		}
	}

	@Override
	public void readSmartDashboard() {
		isTestingEnabled = SmartDashboard.getBoolean(name + " Enable test Mode", false);
		if (isTestingEnabled && (timer.hasWaited(1000) || !enabledTestingMode)) {
			timer.start();
			enabledTestingMode = true;
			reloadPIDFValues();
		}

	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		if (slotIdx == 1)
			lastkDSlot1 = value;
		else
			lastkDSlot0 = value;
		if (isTestingEnabled)
			return super.config_kD(slotIdx, value * safePercentage, timeoutMs);
		else
			return super.config_kD(slotIdx, value, timeoutMs);
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		if (slotIdx == 1)
			lastkISlot1 = value;
		else
			lastkISlot0 = value;
		if (isTestingEnabled)
			return super.config_kI(slotIdx, value * safePercentage, timeoutMs);
		else
			return super.config_kI(slotIdx, value, timeoutMs);
	}

	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		if (slotIdx == 1)
			lastkPSlot1 = value;
		else
			lastkPSlot0 = value;
		if (isTestingEnabled)
			return super.config_kP(slotIdx, value * safePercentage, timeoutMs);
		else
			return super.config_kP(slotIdx, value, timeoutMs);
	}

	@Override
	public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		if (slotIdx == 1)
			lastkFSlot1 = value;
		else
			lastkFSlot0 = value;
		if (isTestingEnabled)
			return super.config_kF(slotIdx, value * safePercentage, timeoutMs);
		else
			return super.config_kF(slotIdx, value, timeoutMs);
	}

	@Override
	public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
		lastPercentOutForward = percentOut;
		if (isTestingEnabled)
			return super.configPeakOutputForward(percentOut * safePercentage, timeoutMs);
		else
			return super.configPeakOutputForward(percentOut, timeoutMs);
	}

	@Override
	public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
		lastPercentOutReverse = percentOut;
		if (isTestingEnabled)
			return super.configPeakOutputReverse(percentOut * safePercentage, timeoutMs);
		else
			return super.configPeakOutputReverse(percentOut, timeoutMs);
	}

	public void reloadPIDFValues() {
		this.config_kP(1, lastkPSlot1, 10);
		this.config_kI(1, lastkISlot1, 10);
		this.config_kD(1, lastkDSlot1, 10);
		this.config_kF(1, lastkFSlot1, 10);
		this.config_kP(0, lastkPSlot0, 10);
		this.config_kI(0, lastkISlot0, 10);
		this.config_kD(0, lastkDSlot0, 10);
		this.config_kF(0, lastkFSlot0, 10);
		this.configPeakOutputForward(lastPercentOutForward, 10);
		this.configPeakOutputReverse(lastPercentOutReverse, 10);
	}

}
