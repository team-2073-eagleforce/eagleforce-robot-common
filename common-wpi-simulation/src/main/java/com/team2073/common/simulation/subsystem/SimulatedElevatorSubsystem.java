package com.team2073.common.simulation.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.periodic.PeriodicRunnable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * An example elevator subsystem
 *
 * The encoder is on the same shaft of the pulley, and the encoder has 1350 tics per inch of elevator travel.
 *
 */
public class SimulatedElevatorSubsystem implements PeriodicRunnable {
    private IMotorControllerEnhanced talon;
    private DigitalInput zeroSensor;
    private Solenoid brake;

    private boolean started;
    private double ticsPerInch = 1350;

    //		UNITS FOR P are in percentages per inch
    private PidfControlLoop pid = new PidfControlLoop(.14, 0, .08, 0, 1);

    public SimulatedElevatorSubsystem(IMotorControllerEnhanced talon, DigitalInput zeroSensor, Solenoid brake) {
        this.talon = talon;
        this.zeroSensor = zeroSensor;
        this.brake = brake;
        pid.setPositionSupplier(() -> talon.getSelectedSensorPosition(0) / ticsPerInch);
    }

    public void set(double setpoint) {
        started = false;
        pid.updateSetPoint(setpoint);
    }

    @Override
    public void onPeriodic() {
        if (!started) {
            brake.set(false);
            started = true;
        }
        pid.updatePID(.01);

        if (zeroSensor.get()) {
            brake.set(true);
            talon.set(ControlMode.PercentOutput, 0);
        } else {
            talon.set(ControlMode.PercentOutput, pid.getOutput());
        }

    }
}
