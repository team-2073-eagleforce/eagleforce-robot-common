package com.team2073.common.controlloop;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.ConversionUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class PidfControlLoop {

    private static final int MAX_FCONDITION_EXCEPTIONS_TO_LOG = 5;
    private static final double LONG_PID_INTERVAL = .2;
    private static final double DEFAULT_INTERVAL = .01;

	private Logger log = LoggerFactory.getLogger(getClass());
	private final RobotContext robotContext = RobotContext.getInstance();

	private boolean active;

	private final double p;
	private final double i;
	private final double d;
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
	private double lastTime = ConversionUtil.msToSeconds(System.currentTimeMillis());

	public PidfControlLoop(double p, double i, double d, double f, double maxOutput) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.maxOutput = maxOutput;
//		robotContext.getDataRecorder().registerRecordable(this);
	}

    public PidfControlLoop(double p, double i, double d, double f, double maxOutput, PositionSupplier positionSupplier) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.maxOutput = maxOutput;
        this.positionSupplier = positionSupplier;
//        robotContext.getDataRecorder().registerRecordable(this);
    }

	public void updatePID(double interval) {

		if (positionSupplier == null)
			Throw.illegalState("PositionSupplier must not be null.");

		position = positionSupplier.currentPosition();
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

	public void setPositionSupplier(PositionSupplier positionSupplier) {
		this.positionSupplier = positionSupplier;
	}

	public interface PositionSupplier {
		double currentPosition();
	}
}
