package com.team2073.common.controlloop;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.util.ConversionUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class PidfControlLoop implements LifecycleAwareRecordable {

	@DataPointIgnore
	private static final int MAX_FCONDITION_EXCEPTIONS_TO_LOG = 5;

	@DataPointIgnore
	private static final double LONG_PID_INTERVAL = .2;

	@DataPointIgnore
	private static final double DEFAULT_INTERVAL = .01;

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
	private Callable<Double> positionSupplier;
	private Callable<Boolean> fCondition;
	private int fConditionExceptionCount;
	private double lastTime = ConversionUtil.msToSeconds(System.currentTimeMillis());

	public PidfControlLoop(double p, double i, double d, double f, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
		RobotContext.getInstance().getDataRecorder().registerRecordable(this);
	}

    public PidfControlLoop(double p, double i, double d, double f, double maxOutput, Callable<Double> positionSupplier) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.maxOutput = maxOutput;
        this.positionSupplier = positionSupplier;
        RobotContext.getInstance().getDataRecorder().registerRecordable(this);
    }

	public void updatePID(double interval) {

		if (positionSupplier == null)
			Throw.illegalState("PositionSupplier must not be null.");

		try {
			position = positionSupplier.call();
		} catch (Exception e) {
            Throw.illegalState("[{}] did not return a valid output.", PositionSupplier.class.getSimpleName());
        }

		error = goal - position;

		output = 0;

		try {
			if(fCondition == null || fCondition.call()){
				output += f;
			}
		} catch (Exception e) {
			fConditionExceptionCount++;
			if (fConditionExceptionCount < MAX_FCONDITION_EXCEPTIONS_TO_LOG)
				log.warn("Exception calling fCondition: ", e);
		}

		output += p * error;

		if (maxIContribution == null)
			output += i * accumulatedError;
		else
			output += Math.min(i * accumulatedError, maxIContribution);

		output += d * errorVelocity;

		accumulatedError += error * (interval);
		errorVelocity = ((error - lastError) / (interval));
		lastError = error;

		if (Math.abs(output) >= maxOutput) {
			if (output > 0) {
				output = maxOutput;
			} else {
				output = -maxOutput;
			}
		}
	}

	public void updatePID(){
		double currentTime = ConversionUtil.msToSeconds(System.currentTimeMillis());
		if(currentTime - lastTime > LONG_PID_INTERVAL){
			updatePID(DEFAULT_INTERVAL);
		}else{
			updatePID(currentTime - lastTime);
		}
		lastTime = currentTime;
	}

	public double getOutput() {
		return output;
	}

	public void updateSetPoint(double newGoal) {
		this.goal = newGoal;
	}

	public double getError() {
		return error;
	}

	public void setMaxIContribution(double maxContribution) {
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

	public void setPositionSupplier(Callable<Double> positionSupplier) {
		this.positionSupplier = positionSupplier;
	}

}
