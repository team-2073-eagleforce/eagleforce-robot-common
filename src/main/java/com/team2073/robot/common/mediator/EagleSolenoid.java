package com.team2073.robot.common.mediator;

import edu.wpi.first.wpilibj.Solenoid;

public class EagleSolenoid extends Solenoid {
    public EagleSolenoid(int channel) {
        super(channel);
    }

    @Override
    public boolean get(){
        return true;
    }
}
