package com.team2073.common.robot.adapter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.util.WPILibVersion;

/**
 * @author pbriggs
 */
public class DriverStationAdapterDefaultImpl implements DriverStationAdapter {

    private static DriverStationAdapterDefaultImpl instance = new DriverStationAdapterDefaultImpl();

    public static DriverStationAdapterDefaultImpl getInstance() {
        return instance;
    }

    private DriverStationAdapterDefaultImpl() {

    }

    @Override
    public boolean isEnabled() {
        return DriverStation.isEnabled();
    }

    @Override
    public boolean isDSAttached() {
        return DriverStation.isDSAttached();
    }

    @Override
    public boolean isFMSAttached() {
        return DriverStation.isFMSAttached();
    }

    @Override
    public Alliance getAlliance() {
        return DriverStation.getAlliance().get();
    }

    @Override
    public int getLocation() {
        return DriverStation.getLocation().getAsInt();
    }

    @Override
    public MatchType getMatchType() {
        return DriverStation.getMatchType();
    }

    @Override
    public int getMatchNumber() {
        return DriverStation.getMatchNumber();
    }

    @Override
    public double getMatchTime() {
        return DriverStation.getMatchTime();
    }
}
