package com.team2073.common.robot.adapter;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;

/**
 * @author pbriggs
 */
public class DriverStationAdapterSimulationImpl implements DriverStationAdapter {

    private static DriverStationAdapterSimulationImpl instance = new DriverStationAdapterSimulationImpl();

    public static DriverStationAdapterSimulationImpl getInstance() {
        return instance;
    }

    private DriverStationAdapterSimulationImpl() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isDSAttached() {
        return true;
    }

    @Override
    public boolean isFMSAttached() {
        // TODO: Maybe create setters?
        return true;
    }

    @Override
    public Alliance getAlliance() {
        // TODO: Maybe create setters?
        return null;
    }

    @Override
    public int getLocation() {
        // TODO: Maybe create setters?
        return 0;
    }

    @Override
    public MatchType getMatchType() {
        // TODO: Maybe create setters?
        return null;
    }

    @Override
    public int getMatchNumber() {
        // TODO: Maybe create setters?
        return 0;
    }

    @Override
    public double getMatchTime() {
        // TODO: Maybe create setters?
        return 0;
    }

}
