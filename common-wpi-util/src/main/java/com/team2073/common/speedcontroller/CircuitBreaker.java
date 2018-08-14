package com.team2073.common.speedcontroller;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.periodic.PeriodicRunner;

// TODO: This should be a wrapper around a BaseMotorController. See LogWrappingCommand for example
public class CircuitBreaker implements PeriodicAware {
	private double minimumVoltageToMove;
	private double minimumVoltageToMoveNegatively;
	private double maxAllowableCurrent;
	private long allowableTimeAtStall;
	private double minTravel;
	private double maxTravel;
	private boolean active = false;
	private boolean hasBeenActive = false;
	private TalonSRX talon;
	private long startingStallTime;


	/**
	 * Used for basic open loop control systems where gravity is negligible.
	 *
	 * @param minimumVoltageToMove
	 * @param maxAllowableAmperage
	 * @param allowableTimeAtStallInMillis
	 * @param talon
	 */
	public CircuitBreaker(double minimumVoltageToMove, double maxAllowableAmperage, long allowableTimeAtStallInMillis, TalonSRX talon) {
		this.minimumVoltageToMove = minimumVoltageToMove;
		this.maxAllowableCurrent = maxAllowableAmperage;
		this.allowableTimeAtStall = allowableTimeAtStallInMillis;
		this.talon = talon;
		PeriodicRunner.registerInstance(this);
	}

	/**
	 * Used for basic open loop control systems where gravity is not negligible.
	 *
	 * @param minimumVoltageToMoveUp
	 * @param minimumVoltageToMoveNegatively
	 * @param maxAllowableAmperage
	 * @param allowableTimeAtStallInMillis
	 * @param talon
	 */
	public CircuitBreaker(double minimumVoltageToMoveUp, double minimumVoltageToMoveNegatively, double maxAllowableAmperage, long allowableTimeAtStallInMillis, TalonSRX talon) {
		this.minimumVoltageToMove = minimumVoltageToMoveUp;
		this.minimumVoltageToMoveNegatively = minimumVoltageToMoveNegatively;
		this.maxAllowableCurrent = maxAllowableAmperage;
		this.allowableTimeAtStall = allowableTimeAtStallInMillis;
		this.talon = talon;
		PeriodicRunner.registerInstance(this);
	}

	/**
	 * Used for closed loop systems where gravity is negligible.
	 *
	 * @param minimumVoltageToMove
	 * @param maxAllowableAmperage
	 * @param allowableTimeAtStallInMillis
	 * @param minTravel
	 * @param maxTravel
	 * @param talon
	 */
	public CircuitBreaker(double minimumVoltageToMove, double maxAllowableAmperage, long allowableTimeAtStallInMillis, double minTravel, double maxTravel, TalonSRX talon) {
		this.minimumVoltageToMove = minimumVoltageToMove;
		this.maxAllowableCurrent = maxAllowableAmperage;
		this.allowableTimeAtStall = allowableTimeAtStallInMillis;
		this.minTravel = minTravel;
		this.maxTravel = maxTravel;
		this.talon = talon;
		PeriodicRunner.registerInstance(this);
	}

	/**
	 * Used for closed loop systems where gravity is not negligible.
	 *
	 * @param minimumVoltageToMoveUp
	 * @param minimumVoltageToMoveNegatively (if the mechanism falls under no load, set this to 0)
	 * @param maxAllowableAmperage
	 * @param allowableTimeAtStallInMillis
	 * @param minTravel
	 * @param maxTravel
	 * @param talon
	 */
	public CircuitBreaker(double minimumVoltageToMoveUp, double minimumVoltageToMoveNegatively, double maxAllowableAmperage, long allowableTimeAtStallInMillis, double minTravel, double maxTravel, TalonSRX talon) {
		this.minimumVoltageToMove = minimumVoltageToMoveUp;
		this.minimumVoltageToMoveNegatively = minimumVoltageToMoveNegatively;
		this.maxAllowableCurrent = maxAllowableAmperage;
		this.allowableTimeAtStall = allowableTimeAtStallInMillis;
		this.minTravel = minTravel;
		this.maxTravel = maxTravel;
		this.talon = talon;
		PeriodicRunner.registerInstance(this);
	}

	@Override
	public void onPeriodic() {
		if (isTryingToMove()) {

			if (talon.getOutputCurrent() > maxAllowableCurrent) {
				startingStallTime = System.currentTimeMillis();
			}

		} else {
			startingStallTime = -1;
		}

		if (System.currentTimeMillis() - startingStallTime > allowableTimeAtStall) {
			active = true;
			hasBeenActive = true;
		} else {
			active = false;
		}

	}

	public boolean isCircuitBreakerActive() {
		return active;
	}

	public boolean hasCircuitBreakerBeenActive() {
		if (hasBeenActive) {
			hasBeenActive = false;
			return true;
		} else
			return false;
	}

	private boolean isTryingToMove() {
		if (minimumVoltageToMoveNegatively == 0)
			return Math.abs(talon.getMotorOutputVoltage()) > minimumVoltageToMove;
		else {
			if (talon.getMotorOutputVoltage() >= 0) {
				return talon.getMotorOutputVoltage() > minimumVoltageToMove;
			} else {
				return Math.abs(talon.getMotorOutputVoltage()) > Math.abs(minimumVoltageToMoveNegatively);
			}

		}
	}


}
