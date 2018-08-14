package com.team2073.common.simulation.speedcontrollers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.simulation.models.Mechanism;

public class SimulationEagleSRX extends TalonSRX {
	private double outputVoltage;
	private double maxOutputForward;
	private double maxOutputReverse;
	private double encoderTicsPerUnitOfMechanism;
	private Mechanism mechanism;
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
	
	public SimulationEagleSRX(int deviceNumber, String name, Mechanism mechanism, int encoderTicsPerUnitOfMechanism) {
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

// REFRENCE THE TALONSRX SOFTWARE REFRENCE MANUAL FOR DETAILS ON PID CALCULATIONS

//	/**
//	* 1ms process for PIDF closed-loop.
//	* @param pid ptr to pid object
//	* @param pos signed integral position (or velocity when in velocity mode).
//	* The target pos/velocity is ramped into the target member from caller's 'in'.
//	* If the CloseLoopRamp in the selected Motor Controller Profile is zero then
//	* there is no ramping applied. (throttle units per ms)
//	* PIDF is traditional, unsigned coefficients for P,i,D, signed for F.
//	* Target pos/velocity is feed forward.
//	*
//	* Izone gives the abilty to autoclear the integral sum if error is wound up.
//	* @param revMotDuringCloseLoopEn nonzero to reverse PID output direction.
//	* @param oneDirOnly when using positive only sensor, keep the closed-loop from outputing negative throttle.
//	*/
//	private void PID_Calc1Ms(PID pid, int pos,int revMotDuringCloseLoopEn, int oneDirOnly)
//	{
//	/* grab selected slot */
//	MotorControlProfile_t * slot = MotControlProf_GetSlot();
//	/* calc error : err = target - pos*/
//	int32_t err = pid->target - pos;
//	pid->err = err;
//	/*abs error */
//	int32_t absErr = err;
//	if(err < 0)
//	absErr = -absErr;
//	/* integrate error */
//	if(0 == pid->notFirst){
//	/* first pass since reset/init */
//	pid->iAccum = 0;
//	/* also tare the before ramp throt */
//	pid->out = BDC_GetThrot(); /* the save the current ramp */
//	}else if((!slot->IZone) || (absErr < slot->IZone) ){
//	/* izone is not used OR absErr is within iZone */
//	pid->iAccum += err;
//	}else{
//	pid->iAccum = 0;
//	}
//	/* dErr/dt */
//	if(pid->notFirst){
//	/* calc dErr */
//	pid->dErr = (err - pid->prevErr);
//	}else{
//	/* clear dErr */
//	pid->dErr = 0;
//	}
//	/* P gain X the distance away from where we want */
//	pid->outBeforRmp = PID_Mux_Unsigned(err, slot->P);
//	if(pid->iAccum && slot->I){
//	/* our accumulated error times I gain. If you want the robot to creep up then pass a nonzero Igain */
//	pid->outBeforRmp += PID_Mux_Unsigned(pid->iAccum, slot->I);
//	}
//	/* derivative gain, if you want to react to sharp changes in error (smooth things out). */
//	pid->outBeforRmp += PID_Mux_Unsigned(pid->dErr, slot->D);
//	/* feedforward on the set point */
//	pid->outBeforRmp += PID_Mux_Signed(pid->target, slot->F);
//	/* arm for next pass */
//	{
//	pid->prevErr = err; /* save the prev error for D */
//	pid->notFirst = 1; /* already serviced first pass */
//	}
//	/* if we are using one-direction sensor, only allow throttle in one dir.If it's the wrong direction, use revMotDuringCloseLoopEn to flip it */
//if(oneDirOnly){
//if(pid->outBeforRmp < 0)
//pid->outBeforRmp = 0;
//}
///* honor the direction flip from control */
//if(revMotDuringCloseLoopEn)
//pid->outBeforRmp = -pid->outBeforRmp;
///* honor closelooprampratem, ramp out towards outBeforRmp */
//if(0 != slot->CloseLoopRampRate){
//if(pid->outBeforRmp >= pid->out){
///* we want to increase our throt */
//int32_t deltaUp = pid->outBeforRmp - pid->out;
//if(deltaUp > slot->CloseLoopRampRate)
//deltaUp = slot->CloseLoopRampRate;
//pid->out += deltaUp;
//}else{
///* we want to decrease our throt */
//int32_t deltaDn = pid->out - pid->outBeforRmp;
//if(deltaDn > slot->CloseLoopRampRate)
//deltaDn = slot->CloseLoopRampRate;
//pid->out -= deltaDn;
//}
//}else{
//pid->out = pid->outBeforRmp;
//}
//}
