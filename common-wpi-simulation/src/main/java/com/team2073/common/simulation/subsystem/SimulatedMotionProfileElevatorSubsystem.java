package com.team2073.common.simulation.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.motionprofiling.SCurveProfileGenerator;
import com.team2073.common.periodic.PeriodicAware;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * @author pbriggs
 */
public class SimulatedMotionProfileElevatorSubsystem implements PeriodicAware {
    double maxVelocity = 6;
    double maxAcceleration = 20;
    double averageAcceleration = 15;
    SCurveProfileGenerator profile;
    MotionProfileControlloop mpc = new MotionProfileControlloop(.005, 0.0001, .0155, .012, .01, 1);
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

        mpc.dataPointCallable(() -> profile.nextPoint(.01));

        mpc.updatePosition(() -> talon.getSelectedSensorPosition(0) / ticsPerInch);
    }

    public void set(double setpoint) {
        this.setpoint = setpoint;
        profile = new SCurveProfileGenerator(
                setpoint, maxVelocity, maxAcceleration, averageAcceleration);
        mpc.stop();
        started = false;
    }

    @Override
    public void onPeriodic() {
        if (!started) {
            mpc.start();
            brake.set(false);
            started = true;
        }

        if (zeroSensor.get()) {
            brake.set(true);
            talon.set(ControlMode.PercentOutput, 0);
        } else {
            talon.set(ControlMode.PercentOutput, mpc.getOutput());
        }

    }
}
