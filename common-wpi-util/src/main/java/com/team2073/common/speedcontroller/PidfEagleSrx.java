package com.team2073.common.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.exception.NotYetImplementedException;

/**
 * @author Preston Briggs
 */
public class PidfEagleSrx extends EagleSRX {

    private PidfControlLoop pid;

    public PidfEagleSrx(int deviceNumber, String name, double safePercentage, PidfControlLoop pid) {
        super(deviceNumber, name, safePercentage);
        this.pid = pid;
    }

    @Override
    public void set(ControlMode mode, double outputValue) {
        if (mode != ControlMode.Position)
            super.set(mode, outputValue);

    }

    @Override
    protected ControlMode getNextControlMode() {
        return ControlMode.PercentOutput;
    }

    @Override
    protected double getNextOutputValue() {
        return pid.getOutput();
    }

    @Override
    public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
        throw new NotYetImplementedException("config_kD");
    }

    @Override
    public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
        throw new NotYetImplementedException("config_kI");
    }

    @Override
    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
        throw new NotYetImplementedException("config_kP");
    }

    @Override
    public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
        throw new NotYetImplementedException("config_kF");
    }

    @Override
    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        throw new NotYetImplementedException("configPeakOutputForward");
    }

    @Override
    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        throw new NotYetImplementedException("configPeakOutputReverse");
    }
}
