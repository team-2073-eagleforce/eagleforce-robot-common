package com.team2073.common.robot.adapter;

import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ReflectionUtil.PrimitiveType;
import com.team2073.common.util.ReflectionUtil.PrimitiveTypeGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pbriggs
 */
public class SmartDashboardAdapterSimulationImpl implements SmartDashboardAdapter {

    private static SmartDashboardAdapterSimulationImpl instance = new SmartDashboardAdapterSimulationImpl();

    public static SmartDashboardAdapterSimulationImpl getInstance() {
        return instance;
    }

    private final Map<String, KeyRegistration> historyMap = new HashMap<>();

    SmartDashboardAdapterSimulationImpl() {

    }

    @Override
    public void putBoolean(String key, boolean value) {
        update(key, value);
    }

    @Override
    public void putString(String key, String value) {
        update(key, value);
    }

    @Override
    public void putNumber(String key, double value) {
        update(key, value);
    }

    private void update(String key, Object value) {
        KeyRegistration history = historyMap.computeIfAbsent(key, k -> new KeyRegistration(value));
        history.add(value);
    }

    public Map<String, KeyRegistration> getHistoryMap() {
        return historyMap;
    }

    public static class KeyRegistration {

        private final PrimitiveTypeGroup type;
        private final List<Object> history = new ArrayList<>();

        public KeyRegistration(Object firstValue) {
            PrimitiveType primitiveType = ReflectionUtil.getPrimitiveType(firstValue);
            type = primitiveType.getGroup();
        }

        public PrimitiveTypeGroup getType() {
            return type;
        }

        public List<Object> getHistory() {
            return Collections.unmodifiableList(history);
        }

        public void add(Object value) {
            history.add(value);
        }
    }

    @Override
    public NetworkTableAdapter getTable(String key) {
        return NetworkTableInstanceAdapterSimulationImpl.getInstance().getTable(key);
    }
}
