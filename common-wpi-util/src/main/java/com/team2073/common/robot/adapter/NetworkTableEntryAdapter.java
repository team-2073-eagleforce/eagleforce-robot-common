package com.team2073.common.robot.adapter;

/**
 * @author pbriggs
 */
public interface NetworkTableEntryAdapter {

    /**
     * Sets the entry's value.
     * @param value the value to set
     * @return False if the entry exists with a different type
     */
    boolean setBoolean(boolean value);

    /**
     * Sets the entry's value.
     * @param value the value to set
     * @return False if the entry exists with a different type
     */
    boolean setDouble(double value);

    /**
     * Sets the entry's value.
     * @param value the value to set
     * @return False if the entry exists with a different type
     */
    boolean setNumber(Number value);

    /**
     * Sets the entry's value.
     * @param value the value to set
     * @return False if the entry exists with a different type
     */
    boolean setString(String value);

}
