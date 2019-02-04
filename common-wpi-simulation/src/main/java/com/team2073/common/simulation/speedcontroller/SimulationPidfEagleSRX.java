package com.team2073.common.simulation.speedcontroller;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.simulation.model.SimulationMechanism;

/**
 * @author Preston Briggs
 */
public class SimulationPidfEagleSRX extends SimulationEagleSRX {

    private PidfControlLoop pid;
    private ControlMode mode;

    public SimulationPidfEagleSRX(String name, SimulationMechanism mechanism, int encoderTicsPerUnitOfMechanism, PidfControlLoop pid) {
        super(name, mechanism, encoderTicsPerUnitOfMechanism);
        this.pid = pid;
        pid.setPositionSupplier(() -> (double) getSelectedSensorPosition(0));
    }

    @Override
    public void set(ControlMode mode, double outputValue) {
        this.mode = mode;
        if (mode != ControlMode.Position) {
            super.set(mode, outputValue);
        }else{
            pid.updateSetPoint(outputValue);
        }
    }

    @Override
    public void onPeriodic() {
        getSelectedSensorPosition(0);
        if (mode == ControlMode.Position) {
            pid.updatePID(.01);
            setOutputPercent(pid.getOutput());
        }

        super.onPeriodic();
    }
}
