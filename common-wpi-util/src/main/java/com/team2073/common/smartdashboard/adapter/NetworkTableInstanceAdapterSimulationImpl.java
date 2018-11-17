package com.team2073.common.smartdashboard.adapter;

/**
 * @author pbriggs
 */
public class NetworkTableInstanceAdapterSimulationImpl implements NetworkTableInstanceAdapter {

    private static NetworkTableInstanceAdapterSimulationImpl instance = new NetworkTableInstanceAdapterSimulationImpl();

    public static NetworkTableInstanceAdapterSimulationImpl getInstance() {
        return instance;
    }

    private NetworkTableInstanceAdapterSimulationImpl() {

    }

    @Override
    public NetworkTableAdapter getTable(String key) {
        return new NetworkTableAdapterSimulationImpl();
    }
}
