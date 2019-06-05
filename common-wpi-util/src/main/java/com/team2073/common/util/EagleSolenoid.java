package com.team2073.common.util;

import edu.wpi.first.wpilibj.Solenoid;

// TODO: What is this and where should it go?
public class EagleSolenoid extends Solenoid {
    public EagleSolenoid(int channel) {
        super(channel);
    }

    @Override
    public boolean get(){
        return true;
    }
}
