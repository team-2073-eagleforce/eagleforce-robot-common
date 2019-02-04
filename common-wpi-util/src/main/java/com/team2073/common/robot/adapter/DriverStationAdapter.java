package com.team2073.common.robot.adapter;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;

/**
 * @author pbriggs
 */
public interface DriverStationAdapter {

    static DriverStationAdapter getInstance() {
        return DriverStationAdapterDefaultImpl.getInstance();
    }

    /**
     * Gets a value indicating whether the Driver Station requires the robot to be enabled.
     *
     * @return True if the robot is enabled, false otherwise.
     */
    boolean isEnabled();

    /**
     * Gets a value indicating whether the Driver Station is attached.
     *
     * @return True if Driver Station is attached, false otherwise.
     */
    boolean isDSAttached();

    /**
     * Gets if the driver station attached to a Field Management System.
     *
     * @return true if the robot is competing on a field being controlled by a Field Management System
     */
    boolean isFMSAttached();

    /**
     * Get the current alliance from the FMS.
     *
     * @return the current alliance
     */
    Alliance getAlliance();

    /**
     * Gets the location of the team's driver station controls.
     *
     * @return the location of the team's driver station controls: 1, 2, or 3
     */
    int getLocation();

    /**
     * Get the match type.
     *
     * @return the match type
     */
    MatchType getMatchType();

    /**
     * Get the match number.
     *
     * @return the match number
     */
    int getMatchNumber();

    /**
     * Return the approximate match time. The FMS does not send an official match time to the robots,
     * but does send an approximate match time. The value will count down the time remaining in the
     * current period (auto or teleop). Warning: This is not an official time (so it cannot be used to
     * dispute ref calls or guarantee that a function will trigger before the match ends) The
     * Practice Match function of the DS approximates the behaviour seen on the field.
     *
     * @return Time remaining in current match period (auto or teleop) in seconds
     */
    double getMatchTime();
}
