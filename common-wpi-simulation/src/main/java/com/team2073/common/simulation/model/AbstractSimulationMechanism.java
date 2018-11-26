package com.team2073.common.simulation.model;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;

/**
 *
 * @author Jason Stanley
 */
public abstract class AbstractSimulationMechanism implements SimulationMechanism, LifecycleAwareRecordable {

	protected Runnable whenSolenoidActive = () -> {};
	protected boolean isSolenoidExtended;

	protected double position = 0;
	protected double velocity = 0;
	protected double acceleration = 0;

	@DataPointIgnore
	protected final double gearRatio;
	@DataPointIgnore
	protected final double massOnSystem;
	@DataPointIgnore
	protected final double velocityConstant;
	@DataPointIgnore
	protected final double torqueConstant;
	@DataPointIgnore
	protected final double motorResistance;
	protected double currentVoltage = 0;

	/**
	 * Units are in terms of RPM, Inches, and Pounds
	 *
	 * @param gearRatio    Should be > 1, from motor to output
	 * @param motor        The Type of motor is the system running on.
	 * @param motorCount   The number of motors for the system.
	 * @param massOnSystem How much weight are we pulling up. (Probably want to overestimate this kV bit)
	 */
	public AbstractSimulationMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem) {
		this.gearRatio = gearRatio;
		this.massOnSystem = massOnSystem;

		velocityConstant = motor.velocityConstant;
		motorResistance = motor.motorResistance;

//		doubles the stall torque to make "super motor" based on motor count
		torqueConstant = motor.torqueConstant * 2 * motorCount;
		RobotContext.getInstance().getDataRecorder().registerRecordable(this);
	}

	@Override
	public boolean isSolenoidExtended() {
		return isSolenoidExtended;
	}

	@Override
	public void whenSolenoidActive(Runnable function) {
		this.whenSolenoidActive = function;
	}

	@Override
	public void setPosition(double position) {
		this.position = position;
	}

	@Override
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		if(isSolenoidExtended()){
			whenSolenoidActive.run();
		}
	}

	@Override
	public double position() {
		return position;
	}

	@Override
	public double velocity() {
		return velocity;
	}

	@Override
	public double acceleration() {
		return acceleration;
	}

	@Override
	public void updateSolenoid(boolean on) {
		isSolenoidExtended = on;
	}
}
