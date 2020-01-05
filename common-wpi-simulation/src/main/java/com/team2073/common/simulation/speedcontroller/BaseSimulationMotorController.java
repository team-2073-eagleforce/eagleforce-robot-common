package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseSimulationMotorController implements SimulationMotorController, LifecycleAwareRecordable, PeriodicRunnable {

    @DataPointIgnore
    public static final double VOLTAGE = 12;
    
    private Logger log = LoggerFactory.getLogger(getClass());
    private final RobotContext robotContext = RobotContext.getInstance();

    @DataPointIgnore
    protected String name;
    protected SimulationMechanism mechanism;
    @DataPointIgnore
    protected double maxVoltageForward = VOLTAGE;
    @DataPointIgnore
    protected double maxVoltageReverse = -VOLTAGE;
    private double outputVoltage;

    public BaseSimulationMotorController(String name, SimulationMechanism mechanism){
        this.mechanism = mechanism;
        this.name = name;
        robotContext.getPeriodicRunner().register(this);
//        robotContext.getDataRecorder().registerRecordable(this);
    }

    @Override
    public void set(ControlMode mode, double outputValue) {

        switch (mode) {
            case Position:
                Throw.notImplemented("Haven't set up native talon pid to work with simulation, use [{}] instead.",
                        SimulationPidfEagleSRX.class.getSimpleName());
                // dead code
                break;
            case PercentOutput:
                outputValue = Math.min(1, outputValue);
                outputValue = Math.max(-1, outputValue);
                setOutputVoltage(toVoltage(outputValue));
                break;
            case Disabled:
                setOutputVoltage(0);
                break;
            default:
                EnumUtil.throwUnknownValueException(mode);
        }
    }

    @Override
    public void onPeriodic() {
        mechanism.updateVoltage(talonOutputVoltage());
    }

    public double talonOutputVoltage() {
        double output = getMotorOutputVoltage();
        if (output >= 0)
            return Math.min(output, maxVoltageForward);
        else
            return Math.max(output, maxVoltageReverse);
    }

    @Override
    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        this.maxVoltageForward = toVoltage(percentOut);
        return null;
    }

    @Override
    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        this.maxVoltageReverse = toVoltage(percentOut);
        return null;
    }

    @Override
    public double getMotorOutputPercent() {
        return toPercent(getMotorOutputVoltage());
    }

    @Override
    public double getMotorOutputVoltage() {
        return outputVoltage;
    }

    protected final void setOutputPercent(double outputPercent) {
        this.outputVoltage = toVoltage(outputPercent);
    }

    protected final void setOutputVoltage(double outputVoltage) {
        this.outputVoltage = outputVoltage;
    }

    private double toPercent(double voltage) {
        return voltage / VOLTAGE;
    }

    private double toVoltage(double percent) {
        return percent * VOLTAGE;
    }

}
