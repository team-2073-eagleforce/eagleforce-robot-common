package com.team2073.common.smartdashboard.adapter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;

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
        return DriverStation.getInstance().isEnabled();
    }

    @Override
    public boolean isDSAttached() {
        return DriverStation.getInstance().isDSAttached();
    }

    @Override
    public boolean isFMSAttached() {
        return DriverStation.getInstance().isFMSAttached();
    }

    @Override
    public Alliance getAlliance() {
        return DriverStation.getInstance().getAlliance();
    }

    @Override
    public int getLocation() {
        return DriverStation.getInstance().getLocation();
    }

    @Override
    public MatchType getMatchType() {
        return DriverStation.getInstance().getMatchType();
    }

    @Override
    public int getMatchNumber() {
        return DriverStation.getInstance().getMatchNumber();
    }

    @Override
    public double getMatchTime() {
        return DriverStation.getInstance().getMatchTime();
    }
}
