package com.team2073.common.position.hold;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team2073.common.assertion.Assert;
import com.team2073.common.position.converter.NoOpPositionConverter;
import com.team2073.common.position.converter.PositionConverter;

import java.util.Collection;

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
	public PIDHoldingStrategy(Collection<IMotorController> motorControllers) {
		this(new NoOpPositionConverter(), motorControllers);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(PositionConverter converter, Collection<IMotorController> motorControllers) {
		super(motorControllers);
		this.converter = validateConverter(converter);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(double initialHoldPosition, Collection<IMotorController> motorControllers) {
		this(new NoOpPositionConverter(), initialHoldPosition, motorControllers);
	}

	/** @see PIDHoldingStrategy */
	public PIDHoldingStrategy(PositionConverter converter, double initialHoldPosition, Collection<IMotorController> motorControllers) {
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
