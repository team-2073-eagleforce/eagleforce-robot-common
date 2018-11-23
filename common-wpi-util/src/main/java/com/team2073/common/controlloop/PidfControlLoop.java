package com.team2073.common.controlloop;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class PidfControlLoop implements LifecycleAwareRecordable {

	@DataPointIgnore
	private static final int MAX_FCONDITION_EXCEPTIONS_TO_LOG = 5;

	private Logger log = LoggerFactory.getLogger(getClass());

	private boolean active;

	@DataPointIgnore
	private final double p;
	@DataPointIgnore
	private final double i;
	@DataPointIgnore
	private final double d;
	@DataPointIgnore
	private final double f;

	private double output;
	private double maxOutput;
	private double goal;
	private double error;
	private double accumulatedError;
	private double errorVelocity;
	private double lastError;
	private double position;
	private Double maxIContribution = null;
	private PositionSupplier positionSupplier;
	private Callable<Boolean> fCondition;
	private int fConditionExceptionCount;

	public PidfControlLoop(double p, double i, double d, double f, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		RobotContext.getInstance().getDataRecorder().registerRecordable(this);
	}

	public void udatePID(double interval) {

		if (positionSupplier == null)
			Throw.illegalState("[{}] must not be null.", PositionSupplier.class.getSimpleName());

		position = positionSupplier.currentPosition();

		error = goal - position;

		output = 0;

		try {
			if(fCondition == null || fCondition.call()){
				output += f;
			}
		} catch (Exception e) {
			// TODO: Jason, should we still continue executing? What happens if we skip f?
			fConditionExceptionCount++;
			if (fConditionExceptionCount < MAX_FCONDITION_EXCEPTIONS_TO_LOG)
				log.warn("Exception calling fCondition: ", e);
		}

		accumulatedError += error * (interval);
		errorVelocity = ((error - lastError) / (interval));

		output += p * error;
		if (maxIContribution == null)
			output += i * accumulatedError;
		else
			output += Math.min(i * accumulatedError, maxIContribution);
		output += d * errorVelocity;

		lastError = error;

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

	public void updateSetPoint(double newGoal) {
		this.goal = newGoal;
	}

	public double getError() {
		return error;
	}

	public void configMaxIContribution(double maxContribution) {
		this.maxIContribution = maxContribution;
	}

	public void resetAccumulatedError(){
		this.accumulatedError = 0;
	}

	/**
	 * The F gain will only be applied if this condition is true, if not specified, F gain will always be used.
	 */
	public void useFCondition(Callable<Boolean> fCondition){
		this.fCondition = fCondition;
	}

	public void setPositionSupplier(PositionSupplier positionSupplier) {
		this.positionSupplier = positionSupplier;
	}

	public interface PositionSupplier {
		double currentPosition();
	}
}
