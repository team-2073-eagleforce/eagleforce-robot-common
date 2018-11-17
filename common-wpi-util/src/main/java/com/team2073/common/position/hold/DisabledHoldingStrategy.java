package com.team2073.common.position.hold;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team2073.common.assertion.Assert;

import java.util.Collection;

/**
 * Disables the motor upon hold. Can be customized with a desired {@link NeutralMode}
 * of {@link NeutralMode#Coast} or {@link NeutralMode#Brake}.
 * 
 * @author Preston Briggs
 */
public class DisabledHoldingStrategy extends MotorAwareHoldingStrategy {

	private NeutralMode mode;
	
	/** @see DisabledHoldingStrategy */
	public DisabledHoldingStrategy(Collection<IMotorController> motorControllers) {
		this(0, motorControllers);
	}

	/** @see DisabledHoldingStrategy */
	public DisabledHoldingStrategy(double initialHoldPosition, Collection<IMotorController> motorControllers) {
		super(initialHoldPosition, motorControllers);
	}

	/** @see DisabledHoldingStrategy */ 
	public DisabledHoldingStrategy(NeutralMode mode, Collection<IMotorController> motorControllers) {
		this(-1, mode, motorControllers);
	}

	/** @see DisabledHoldingStrategy */ 
	public DisabledHoldingStrategy(double initialHoldPosition, NeutralMode mode, Collection<IMotorController> motorControllers) {
		super(initialHoldPosition, motorControllers);
		Assert.assertNotNull(mode, "mode");
		this.mode = mode;
	}
	
	@Override
	public double getHoldPosition() {
		return -1;
	}
	
	@Override
	public void holdPosition() {
		doToMotors(motor -> {
			motor.set(ControlMode.Disabled, 0);
			if(mode != null)
				motor.setNeutralMode(mode);
		});
	}

}
