package com.team2073.common.controller;

import com.team2073.common.sim.SimulationComponent;

public interface UsbController extends SimulationComponent {
    boolean getRawButton(int port);
    double getRawAxis(int axis);
    int getPOV();


}

