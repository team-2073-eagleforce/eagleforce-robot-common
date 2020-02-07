package com.team2073.common.motionmagic;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.position.converter.PositionConverter;

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

    public void update(double setpoint) {
        this.setpoint = setpoint;
        talon.set(ControlMode.MotionMagic, positionConverter.asTics(setpoint));
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double currentPosition() {
        return positionConverter.asPosition(talon.getSelectedSensorPosition());
    }

    public double currentVelocity() {
        return positionConverter.asPosition(talon.getSelectedSensorVelocity()*10);
    }


}
