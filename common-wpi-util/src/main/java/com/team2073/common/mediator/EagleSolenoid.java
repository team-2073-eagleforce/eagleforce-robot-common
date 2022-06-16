package com.team2073.common.mediator;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import sun.security.pkcs11.Secmod;

// TODO: What is this and where should it go?
public class EagleSolenoid extends Solenoid {
    public EagleSolenoid(PneumaticsModuleType moduleType, int channel) {
        super(moduleType, channel);
    }

    @Override
    public boolean get(){
        return true;
    }
}
