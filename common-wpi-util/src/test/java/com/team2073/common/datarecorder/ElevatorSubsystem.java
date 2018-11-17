package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.model.DataPoint;

/**
 * @author Preston Briggs
 */
public class ElevatorSubsystem {

    // This field will be named: position
    private double position;

    // This field will be named: v
    @DataPoint(name = "v")
    private double velocity;
}
