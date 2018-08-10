package com.team2073.common.controllers.mapping;

import com.team2073.common.assertion.Assert;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import edu.wpi.first.wpilibj.Joystick;

import java.util.LinkedHashSet;

/**
 * @author pbriggs
 */
public class ControllerMappingRegistry <T extends Joystick> implements SmartDashboardAware {
    LinkedHashSet<ControllerMapping> instanceList = new LinkedHashSet<ControllerMapping>();

    public enum MappingType{
        TEST,
        DRIVE,
        MISC;
    }


    public void registerInstance(ControllerMapping instance, T controller) {
        Assert.assertNotNull(instance, "instance");
        instanceList.add(instance);
        this.controller = controller;
    }

    @Override
    public void updateSmartDashboard() {

    }

    @Override
    public void readSmartDashboard() {
        // TODO Auto-generated method stub

    }
}
