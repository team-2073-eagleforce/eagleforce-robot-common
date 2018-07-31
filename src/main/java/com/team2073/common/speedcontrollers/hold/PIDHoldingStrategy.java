package com.team2073.common.speedcontrollers.hold;

import java.util.Collection;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.team2073.common.assertion.Assert;
import com.team2073.common.position.NoOpPositionConverter;
import com.team2073.common.position.PositionConverter;
import com.team2073.common.subsys.MotorAwareHoldingStrategy;

/**
 * Uses a PID setpoint to hold a position. By default this will use tics in 
 * {@link #getHoldPosition()} and {@link #setHoldPosition(double)}. To use a positional
 * value such as degrees, inches, etc., supply a {@code PositionConverter}.
 *
 * @author Preston Briggs
 */
public class PIDHoldingStrategy extends MotorAwareHoldingStrategy {

	private final PositionConverter converter;
	
	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(Collection<BaseMotorController> motorControllers) {
		this(new NoOpPositionConverter(), motorControllers);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(PositionConverter converter, Collection<BaseMotorController> motorControllers) {
		super(motorControllers);
		this.converter = validateConverter(converter);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(double initialHoldPosition, Collection<BaseMotorController> motorControllers) {
		this(new NoOpPositionConverter(), initialHoldPosition, motorControllers);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(PositionConverter converter, double initialHoldPosition, Collection<BaseMotorController> motorControllers) {
		super(initialHoldPosition, motorControllers);
		this.converter = validateConverter(converter);
	}
	
	private PositionConverter validateConverter(PositionConverter converter) {
		Assert.assertNotNull(converter, "converter");
		PositionConverter.assertConversions(converter);
		return converter;
	}

	@Override
	public void holdPosition() {
		int tics = converter.asTics(getHoldPosition());
		doToMotors(motor -> motor.set(ControlMode.Position, tics));
	}
	
}
