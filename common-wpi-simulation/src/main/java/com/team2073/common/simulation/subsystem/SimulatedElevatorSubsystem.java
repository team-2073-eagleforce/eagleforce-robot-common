package com.team2073.common.simulation.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.periodic.PeriodicAware;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * An example elevator subsystem
 * <p>
 * The encoder is on the same shaft of the pulley, and the encoder has 1350 tics per inch of elevator travel.
 * <p/>
 */
public class SimulatedElevatorSubsystem implements PeriodicAware {
    private IMotorControllerEnhanced talon;
    private DigitalInput zeroSensor;
    private Solenoid brake;

    private boolean started;
    private double ticsPerInch = 1350;
    private double setpoint;

    //		UNITS FOR P are in percentages per inch
    private PidfControlLoop pid = new PidfControlLoop(.023, 0, .02, 0, 10, 1);

    public SimulatedElevatorSubsystem(IMotorControllerEnhanced talon, DigitalInput zeroSensor, Solenoid brake) {
        this.talon = talon;
        this.zeroSensor = zeroSensor;
        this.brake = brake;
    }

    public void set(double setpoint) {
        this.setpoint = setpoint;
        pid.stopPID();
        started = false;
    }

    @Override
    public void onPeriodic() {
        if (!started) {
            pid.startPID(setpoint);
            brake.set(false);
            started = true;
        }
        pid.setNewPosition(talon.getSelectedSensorPosition(0) / ticsPerInch);

        if (zeroSensor.get()) {
            brake.set(true);
            talon.set(ControlMode.PercentOutput, 0);
        } else {
            talon.set(ControlMode.PercentOutput, pid.getOutput());
        }

    }
}
