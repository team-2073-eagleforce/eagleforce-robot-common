package com.team2073.common.robot.adapter;

/**
 * @author pbriggs
 */
public interface NetworkTableAdapter {

    /**
     * Gets the entry for a subkey.
     * @param key the key name
     * @return Network table entry.
     */
    NetworkTableEntryAdapter getEntry(String key);

    /**
     * Returns the table at the specified key. If there is no table at the
     * specified key, it will create a new table
     *
     * @param key the name of the table relative to this one
     * @return a sub table relative to this one
     */
    NetworkTableAdapter getSubTable(String key);

}
