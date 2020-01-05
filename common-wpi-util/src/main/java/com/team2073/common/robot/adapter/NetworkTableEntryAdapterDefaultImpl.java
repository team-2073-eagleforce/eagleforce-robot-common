package com.team2073.common.robot.adapter;

import edu.wpi.first.networktables.NetworkTableEntry;

/**
 * @author pbriggs
 */
public class NetworkTableEntryAdapterDefaultImpl implements NetworkTableEntryAdapter {

    private NetworkTableEntry delegate;

    NetworkTableEntryAdapterDefaultImpl(NetworkTableEntry delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean setBoolean(boolean value) {
        return delegate.setBoolean(value);
    }

    @Override
    public boolean setDouble(double value) {
        return delegate.setDouble(value);
    }

    @Override
    public boolean setNumber(Number value) {
        return delegate.setNumber(value);
    }

    @Override
    public boolean setString(String value) {
        return delegate.setString(value);
    }
}
