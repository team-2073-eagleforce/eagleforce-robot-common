package com.team2073.common.motionmagic;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.position.converter.PositionConverter;

public class MotionMagicHandler {

    private TalonSRX talonSRX;
    private double setpoint;
    private PositionConverter positionConverter;

    public MotionMagicHandler(TalonSRX talonSRX, PositionConverter positionConverter, int SCurveStrength,
                              double maxVelocity, double maxAcceleration) {

        this.talonSRX = talonSRX;
        this.positionConverter = positionConverter;
        talonSRX.configMotionCruiseVelocity((int) (positionConverter.asTics(maxVelocity) * 0.1), 100);
        talonSRX.configMotionAcceleration((int) (positionConverter.asTics(maxAcceleration) * 0.1), 100);
        talonSRX.configMotionSCurveStrength(SCurveStrength);
    }

    /**
     *
     * @param setpoint      Degrees
     * @param feedForward
     */

    public void update(double setpoint, double feedForward) {
        this.setpoint = setpoint;
        talonSRX.set(ControlMode.MotionMagic, positionConverter.asTics(setpoint), DemandType.ArbitraryFeedForward, feedForward);
    }

    public void update(double setpoint) {
        this.setpoint = setpoint;
        talonSRX.set(ControlMode.MotionMagic, positionConverter.asTics(setpoint));
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double currentPosition() {
        return positionConverter.asPosition(talonSRX.getSelectedSensorPosition());
    }

    public double currentVelocity() {
        return positionConverter.asPosition(talonSRX.getSelectedSensorVelocity()*10);
    }


}
