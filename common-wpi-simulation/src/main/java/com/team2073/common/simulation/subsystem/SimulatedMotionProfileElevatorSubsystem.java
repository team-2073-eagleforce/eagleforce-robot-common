package com.team2073.common.simulation.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.motionprofiling.ProfileConfiguration;
import com.team2073.common.motionprofiling.TrapezoidalProfileManager;
import com.team2073.common.periodic.PeriodicRunnable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * @author Jason Stanley
 */
public class SimulatedMotionProfileElevatorSubsystem implements PeriodicRunnable {
    private static final double MAX_VELOCITY = 14;
    private static final double MAX_ACCELERATION = 10;
    public static final double DT = .01;
    private TrapezoidalProfileManager tpm;
    private MotionProfileControlloop mpc = new MotionProfileControlloop(0.30, 0.0, 0.0684931507, .02, 1);
    private IMotorControllerEnhanced talon;
    private DigitalInput zeroSensor;
    private Solenoid brake;
    private boolean started;
    private double ticsPerInch = 1350;
    private double setpoint;

    public SimulatedMotionProfileElevatorSubsystem(IMotorControllerEnhanced talon, DigitalInput zeroSensor, Solenoid brake) {
        this.talon = talon;
        this.zeroSensor = zeroSensor;
        this.brake = brake;
        tpm = new TrapezoidalProfileManager(mpc, new ProfileConfiguration(MAX_VELOCITY, MAX_ACCELERATION, DT), this::position);
    }

    private double position(){
        return talon.getSelectedSensorPosition(0)/ticsPerInch;
    }

    public void set(double setpoint) {
        this.setpoint = setpoint;
        tpm.setPoint(setpoint);
    }

    @Override
    public void onPeriodic() {
        if (!started) {
            brake.set(false);
            started = true;
        }
        tpm.newOutput();
        if (zeroSensor.get()) {
            brake.set(true);
            talon.set(ControlMode.PercentOutput, 0);
        } else {
            talon.set(ControlMode.PercentOutput, tpm.getOutput());
        }

    }
}
