package com.team2073.common.robot.adapter;

import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * @author pbriggs
 */
public class NetworkTableInstanceAdapterDefaultImpl implements NetworkTableInstanceAdapter {

    private static NetworkTableInstanceAdapterDefaultImpl instance = new NetworkTableInstanceAdapterDefaultImpl();

    public static NetworkTableInstanceAdapterDefaultImpl getInstance() {
        return instance;
    }

    private NetworkTableInstanceAdapterDefaultImpl() {

    }

    @Override
    public NetworkTableAdapter getTable(String key) {
        return new NetworkTableAdapterDefaultImpl(NetworkTableInstance.getDefault().getTable(key));
    }

}
