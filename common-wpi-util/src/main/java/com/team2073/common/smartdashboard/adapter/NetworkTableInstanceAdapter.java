package com.team2073.common.smartdashboard.adapter;

/**
 * @author pbriggs
 */
public interface NetworkTableInstanceAdapter {

    /**
     * Gets the table with the specified key.
     *
     * @param key the key name
     * @return The network table
     */
    NetworkTableAdapter getTable(String key);

}
