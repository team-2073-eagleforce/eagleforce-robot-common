package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.simulation.model.SimulationMechanism;
import com.team2073.common.util.EnumUtil;

public abstract class BaseSimulationMotorController implements SimulationMotorController {

    protected String name;
    protected SimulationMechanism mechanism;
    protected double maxVoltageForward = 12;
    protected double maxVoltageReverse = -12;
    protected double outputVoltage;

    public BaseSimulationMotorController(String name, SimulationMechanism mechanism){
            this.mechanism = mechanism;
            this.name = name;
    }

    @Override
    public void set(ControlMode mode, double outputValue) {
        outputValue = Math.min(1, outputValue);
        outputValue = Math.max(-1, outputValue);

        switch (mode) {
            case Position:
                throw new NotYetImplementedException("Haven't set up native talon pid to work with simulation, use our PIDF controller instead.");
            case PercentOutput:
                outputVoltage = 12 * outputValue;
                break;
            default:
                EnumUtil.throwUnknownValueException(mode);
        }
        mechanism.updateVoltage(talonOutputVoltage());
    }

    public double talonOutputVoltage() {
        if (outputVoltage >= 0)
            return Math.min(outputVoltage, maxVoltageForward);
        else
            return Math.max(outputVoltage, maxVoltageReverse);
    }

    @Override
    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        this.maxVoltageForward = percentOut * 12;
        return null;
    }

    @Override
    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        this.maxVoltageReverse = percentOut * 12;
        return null;
    }


    @Override
    public double getMotorOutputPercent() {
        return outputVoltage / 12;
    }

    @Override
    public double getMotorOutputVoltage() {
        return outputVoltage;
    }



}
