package com.team2073.common.controlloop;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class PidfControlLoop {
	private double p;
	private double i;
	private double d;
	private double f;

	private double output;
	private double maxOutput;
	private TalonSRX talon;
	private double goal;
	private double error;
	private double accumulatedError;
	private double errorVelocity;
	private double lastError;
	private long intervalInMilis;
	private Thread periodic;
	private double position;

	/**
	 * 
	 * @param p
	 * @param i
	 * @param d
	 * @param f
	 * @param talon
	 *            if a different input of error is desired, pass in null for talon
	 * @param intervalInMilis
	 * @param maxOutput
	 *            <p>
	 *            goal is in units of encoder tics if using a talon
	 */
	public PidfControlLoop(double p, double i, double d, double f, TalonSRX talon, long intervalInMilis, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		this.talon = talon;
		if (intervalInMilis <= 0)
			intervalInMilis = 1;
		this.intervalInMilis = intervalInMilis;
		periodic = new Thread(new Runnable() {
			public void run() {
				while (true) {
					pidCycle();
					try {
						Thread.sleep(PidfControlLoop.this.intervalInMilis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private void pidCycle() {
		if (talon != null)
			position = talon.getSelectedSensorPosition(0);
		error = goal - position;

		output = 0;
		output += f;
		output += p * error;
		output += i * accumulatedError;
		output += d * errorVelocity;
		
		output = (output / (1000.));
		
		accumulatedError += error;
		errorVelocity = (double) ((error - lastError) / (intervalInMilis));
		error = lastError;

		if (Math.abs(output) >= maxOutput) {
			if (output > 0) {
				output = maxOutput;
			} else {
				output = -maxOutput;
			}
		}
	}

	public double getOutput() {
		return output;
	}

	public void setNewPosition(double position) {
		this.position = position;
	}

	public void startPID(double goal) {
		this.goal = goal;
		if(!periodic.isAlive())
			periodic.start();
	}

	public void stopPID() {
		periodic.interrupt();
	}

//	public class PID {
//		private double target;
//		private double error;
//		private double position;
//		private double previousPosition;
//		private double iAccumulation;
//		private double kPSlot0 = 0;
//		private double kISlot0 = 0;
//		private double kDSlot0 = 0;
//		private double kFSlot0 = 0;
//		private double kPSlot1 = 0;
//		private double kISlot1 = 0;
//		private double kDSlot1 = 0;
//		private double kFSlot1 = 0;
//		private int currentSlot = 0;
//		private double outputInNativeUnits;
//		// Use this to reset D and I gains (previousError and iAccum)
//		private boolean isFirstRun;
//		private double dError;
//
//		public void setCurrentSlot(int slot) {
//			currentSlot = slot;
//		}
//
//		public void setKP(double value, int slot) {
//			if (slot == 0)
//				kPSlot0 = value;
//			else if (slot == 1)
//				kPSlot1 = value;
//		}
//
//		public void setTarget(double target) {
//			this.target = target;
//		}
//
//		public void setKD(double value, int slot) {
//			if (slot == 0)
//				kDSlot0 = value;
//			else if (slot == 1)
//				kDSlot1 = value;
//		}
//
//		public void setKI(double value, int slot) {
//			if (slot == 0)
//				kISlot0 = value;
//			else if (slot == 1)
//				kISlot1 = value;
//		}
//
//		public void setKF(double value, int slot) {
//			if (slot == 0)
//				kFSlot0 = value;
//			else if (slot == 1)
//				kFSlot1 = value;
//		}
//
//		private double getCurrentKP() {
//			if (currentSlot == 0)
//				return kPSlot0;
//			else if (currentSlot == 1)
//				return kPSlot1;
//			else
//				return 0;
//		}
//
//		private double getCurrentKI() {
//			if (currentSlot == 0)
//				return kISlot0;
//			else if (currentSlot == 1)
//				return kISlot1;
//			else
//				return 0;
//		}
//
//		private double getCurrentKD() {
//			if (currentSlot == 0)
//				return kDSlot0;
//			else if (currentSlot == 1)
//				return kDSlot1;
//			else
//				return 0;
//		}
//
//		private double getCurrentKF() {
//			if (currentSlot == 0)
//				return kFSlot0;
//			else if (currentSlot == 1)
//				return kFSlot1;
//			else
//				return 0;
//		}
//
//		private double calcNativeOutput() {
//			// Should be run on a 1ms cycle, so, basically run this as often as the
//			// simulator looks for an new voltage
//			if (!isFirstRun) {
//				/* calc dError and iAccumulation */
//				dError = previousPosition - position;
//
//			} else {
//				/* clear dError */
//				dError = 0;
//				iAccumulation = 0;
//			}
//			double nativeOutput = error * getCurrentKP();
//			nativeOutput += iAccumulation * getCurrentKI();
//			nativeOutput += dError * getCurrentKD();
//			nativeOutput += target * getCurrentKF();
//			outputInNativeUnits = nativeOutput;
//			return nativeOutput;
//		}
//
//		private double convertNativeOutToVolts(double nativeOutput) {
//			return 12 * (nativeOutput / 1023);
//		}
//
//	}
//}

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


}
