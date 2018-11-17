package com.team2073.common.smartdashboard.adapter;

import edu.wpi.first.networktables.NetworkTable;

/**
 * @author pbriggs
 */
public class NetworkTableAdapterDefaultImpl implements NetworkTableAdapter {

    private NetworkTable delegate;

    NetworkTableAdapterDefaultImpl(NetworkTable delegate) {
        this.delegate = delegate;
    }

    @Override
    public NetworkTableEntryAdapter getEntry(String key) {
        return new NetworkTableEntryAdapterDefaultImpl(delegate.getEntry(key));
    }

    @Override
    public NetworkTableAdapter getSubTable(String key) {
        return new NetworkTableAdapterDefaultImpl(delegate.getSubTable(key));
    }
}
