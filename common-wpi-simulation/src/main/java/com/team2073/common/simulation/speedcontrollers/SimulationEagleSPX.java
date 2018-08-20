package com.team2073.common.simulation.speedcontrollers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2073.common.simulation.models.SimulationMechanism;

public class SimulationEagleSPX extends VictorSPX {
	private double outputVoltage;
	private double maxOutputForward;
	private double maxOutputReverse;
	private double encoderTicsPerUnitOfMechanism;
	private SimulationMechanism mechanism;
	private String name;
	private PID pid = new PID();

	public double talonOutputVoltage() {
		if(outputVoltage >= 0 )
			return Math.min(outputVoltage, maxOutputForward);
		else 
			return Math.max(outputVoltage, maxOutputReverse);
	}

	/**
	 * Simulated TalonSRX
	 * 
	 * @param deviceNumber
	 * @param name
	 *            This is the name the Talon will be referred to in logging.
	 * @param mechanism
	 *            The mechanism that is being controlled by this talon
	 * @param encoderTicsPerUnitOfMechanism
	 *            How many encoder tics are expected per unit of the mechanism
	 *            (usually inches or rotations, ie: Elevator tics/inch, pivotingArm:
	 *            ticsPerRevolutionOfArm, etc)
	 * 
	 *            <pre>
	 *            If a specific method is causing errors, or spamming the console
	 *            while you are simulating, make sure that that method is overridden
	 *            and either executes the appropriate logic or simply doesn't call
	 *            super if the method is handled elsewhere. </post>
	 */
	
	public SimulationEagleSPX(int deviceNumber, String name, SimulationMechanism mechanism, int encoderTicsPerUnitOfMechanism) {
			super(deviceNumber);
			this.mechanism = mechanism;
			this.name = name;
			this.encoderTicsPerUnitOfMechanism = encoderTicsPerUnitOfMechanism;
	}

	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		pid.setKP(value, slotIdx);
		return null;
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		pid.setKI(value, slotIdx);
		return null;
	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		pid.setKD(value, slotIdx);
		return null;
	}

	@Override
	public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		pid.setKF(value, slotIdx);
		return null;
	}

	@Override
	public void set(ControlMode mode, double outputValue) {
		switch (mode) {
			case Position:
				pid.setTarget(outputValue);
				outputVoltage = pid.convertNativeOutToVolts(pid.calcNativeOutput());
				break;
			case PercentOutput:
				outputVoltage = 12 * outputValue;
				break;
			default:
				outputVoltage = 0;
				break;
		}
	}
	
	@Override
	public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
		this.maxOutputForward = percentOut;
		return null;
	}
	
	@Override
	public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
		this.maxOutputReverse = percentOut;
		return null;
	}
	
	@Override
	public void selectProfileSlot(int slotIdx, int pidIdx) {
		pid.setCurrentSlot(slotIdx);
	}
	
	@Override
	public int getSelectedSensorPosition(int pidIdx) {
		return (int) Math.round(mechanism.position() * encoderTicsPerUnitOfMechanism);
	}

	@Override
	public double getMotorOutputVoltage() {
		return outputVoltage;
	}
	
	@Override
	public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		return null;
	}
	
	public class PID {
		private double target;
		private double error;
		private double position;
		private double previousPosition;
		private double iAccumulation;
		private double kPSlot0 = 0;
		private double kISlot0 = 0;
		private double kDSlot0 = 0;
		private double kFSlot0 = 0;
		private double kPSlot1 = 0;
		private double kISlot1 = 0;
		private double kDSlot1 = 0;
		private double kFSlot1 = 0;
		private int currentSlot = 0;
		private double outputInNativeUnits;
		// Use this to reset D and I gains (previousError and iAccum)
		private boolean isFirstRun;
		private double dError;

		public void setCurrentSlot(int slot) {
			currentSlot = slot;
		}

		public void setKP(double value, int slot) {
			if (slot == 0)
				kPSlot0 = value;
			else if (slot == 1)
				kPSlot1 = value;
		}

		public void setTarget(double target) {
			this.target = target;
		}

		public void setKD(double value, int slot) {
			if (slot == 0)
				kDSlot0 = value;
			else if (slot == 1)
				kDSlot1 = value;
		}

		public void setKI(double value, int slot) {
			if (slot == 0)
				kISlot0 = value;
			else if (slot == 1)
				kISlot1 = value;
		}

		public void setKF(double value, int slot) {
			if (slot == 0)
				kFSlot0 = value;
			else if (slot == 1)
				kFSlot1 = value;
		}

		private double getCurrentKP() {
			if (currentSlot == 0)
				return kPSlot0;
			else if (currentSlot == 1)
				return kPSlot1;
			else
				return 0;
		}

		private double getCurrentKI() {
			if (currentSlot == 0)
				return kISlot0;
			else if (currentSlot == 1)
				return kISlot1;
			else
				return 0;
		}

		private double getCurrentKD() {
			if (currentSlot == 0)
				return kDSlot0;
			else if (currentSlot == 1)
				return kDSlot1;
			else
				return 0;
		}

		private double getCurrentKF() {
			if (currentSlot == 0)
				return kFSlot0;
			else if (currentSlot == 1)
				return kFSlot1;
			else
				return 0;
		}

		private double calcNativeOutput() {
			// Should be run on a 1ms cycle, so, basically run this as often as the
			// simulator looks for an new voltage
			if (!isFirstRun) {
				/* calc dError and iAccumulation */
				dError = previousPosition - position;

			} else {
				/* clear dError */
				dError = 0;
				iAccumulation = 0;
			}
			double nativeOutput = error * getCurrentKP();
			nativeOutput += iAccumulation * getCurrentKI();
			nativeOutput += dError * getCurrentKD();
			nativeOutput += target * getCurrentKF();
			outputInNativeUnits = nativeOutput;
			return nativeOutput;
		}

		private double convertNativeOutToVolts(double nativeOutput) {
			return 12 * (nativeOutput / 1023);
		}

	}
}
