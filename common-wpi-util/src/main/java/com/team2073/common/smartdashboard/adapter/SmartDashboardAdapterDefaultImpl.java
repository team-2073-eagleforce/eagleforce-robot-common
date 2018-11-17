package com.team2073.common.smartdashboard.adapter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A basic {@link SmartDashboardAdapter} that simply delegates to the {@link SmartDashboard}.
 * This is what you will use when actually running on the robot and you do not need to do any
 * special configuration to get this working, it is the default.
 * @see SmartDashboardAdapter
 */
public class SmartDashboardAdapterDefaultImpl implements SmartDashboardAdapter {

    private static SmartDashboardAdapter instance;

    public static SmartDashboardAdapter getInstance() {
        if (instance == null)
            instance = new SmartDashboardAdapterDefaultImpl();
        return instance;
    }

    private SmartDashboardAdapterDefaultImpl() {

    }

    @Override
    public NetworkTableAdapter getTable(String key) {
        return NetworkTableInstanceAdapterDefaultImpl.getInstance().getTable(key);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        SmartDashboard.putBoolean(key, value);
    }

    @Override
    public void putString(String key, String value) {
        SmartDashboard.putString(key, value);
    }

    @Override
    public void putNumber(String key, double value) {
        SmartDashboard.putNumber(key, value);
    }
}
