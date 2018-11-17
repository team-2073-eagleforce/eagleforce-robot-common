package com.team2073.common.smartdashboard.adapter;

/**
 * @author pbriggs
 */
public class NetworkTableAdapterSimulationImpl implements NetworkTableAdapter {

    NetworkTableAdapterSimulationImpl() {

    }

    @Override
    public NetworkTableEntryAdapter getEntry(String key) {
        // TODO
        return new NetworkTableEntryAdapterSimulationImpl();
    }

    @Override
    public NetworkTableAdapter getSubTable(String key) {
        // TODO
        return new NetworkTableAdapterSimulationImpl();
    }
}
