package com.team2073.common.subsys;

import java.util.Collection;
import java.util.function.Consumer;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.team2073.common.assertion.Assert;
import com.team2073.common.speedcontrollers.hold.BaseHoldingStrategy;

/**
 * A holding strategy providing base functionality for any strategies that require 
 * access to motor controllers.
 * 
 * @author Preston Briggs
 */
public abstract class MotorAwareHoldingStrategy extends BaseHoldingStrategy {
	
	private Collection<BaseMotorController> motorList;

	/** @see MotorAwareHoldingStrategy */
	public MotorAwareHoldingStrategy(Collection<BaseMotorController> motorControllers) {
		setMotors(motorControllers);
	}

	/** @see MotorAwareHoldingStrategy */
	public MotorAwareHoldingStrategy(double initialHoldPosition, Collection<BaseMotorController> motorControllers) {
		super(initialHoldPosition);
		setMotors(motorControllers);
	}
	
	protected void doToMotors(Consumer<BaseMotorController> function) {
		motorList.forEach(mtr -> function.accept(mtr));
	}
	
	private void setMotors(Collection<BaseMotorController> motorControllers) {
		Assert.assertNotNull(motorControllers, "motorControllers");
		motorList = motorControllers;
	}

}
