package com.team2073.common.position.hold;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team2073.common.assertion.Assert;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A holding strategy providing base functionality for any strategies that require 
 * access to motor controllers.
 * 
 * @author Preston Briggs
 */
public abstract class MotorAwareHoldingStrategy extends BaseHoldingStrategy {
	
	private Collection<IMotorController> motorList;

	/** @see MotorAwareHoldingStrategy */
	public MotorAwareHoldingStrategy(Collection<IMotorController> motorControllers) {
		setMotors(motorControllers);
	}

	/** @see MotorAwareHoldingStrategy */
	public MotorAwareHoldingStrategy(double initialHoldPosition, Collection<IMotorController> motorControllers) {
		super(initialHoldPosition);
		setMotors(motorControllers);
	}
	
	protected void doToMotors(Consumer<IMotorController> function) {
		motorList.forEach(mtr -> function.accept(mtr));
	}
	
	private void setMotors(Collection<IMotorController> motorControllers) {
		Assert.assertNotNull(motorControllers, "motorControllers");
		motorList = motorControllers;
	}

}
