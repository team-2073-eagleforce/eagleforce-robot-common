package com.team2073.common.motionmagic;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.position.converter.PositionConverter;
import edu.wpi.first.wpilibj.util.WPILibVersion;

public class MotionMagicHandler {

    private BaseTalon talon;
    private double setpoint;
    private PositionConverter positionConverter;

    public MotionMagicHandler(BaseTalon talon, PositionConverter positionConverter, int SCurveStrength,
                              double maxVelocity, double maxAcceleration) {
        this.talon = talon;
        this.positionConverter = positionConverter;
        this.talon.configMotionCruiseVelocity((int) (positionConverter.asTics(maxVelocity) * 0.1), 100);
        this.talon.configMotionAcceleration((int) (positionConverter.asTics(maxAcceleration) * 0.1), 100);
        this.talon.configMotionSCurveStrength(SCurveStrength);
    }

    /**
     *
     * @param setpoint      Degrees
     * @param feedForward
     */

    public void update(double setpoint, double feedForward) {
        this.setpoint = setpoint;
        talon.set(ControlMode.MotionMagic, positionConverter.asTics(setpoint), DemandType.ArbitraryFeedForward, feedForward);
    }

    /**
     *
     * @param kp
     * @param kv in % out per human readable velocity unit
     */
    public void setGains(double kp, double kv){
        talon.config_kF(0, (1023*kv)/(positionConverter.asTics(1)/10d));
        talon.config_kP(0, (1023d*kp)/positionConverter.asTics(1));
    }
    public void update(double setpoint) {
        this.setpoint = setpoint;
        talon.set(ControlMode.MotionMagic, positionConverter.asTics(setpoint));
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double currentPosition() {
        return positionConverter.asPosition((int)talon.getSelectedSensorPosition());
    }

    public double currentVelocity() {
        return positionConverter.asPosition((int)talon.getSelectedSensorVelocity()*10);
    }

}
