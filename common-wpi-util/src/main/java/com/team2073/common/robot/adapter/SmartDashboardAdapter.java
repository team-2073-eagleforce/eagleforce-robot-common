package com.team2073.common.robot.adapter;

import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ReflectionUtil.PrimitiveType;
import com.team2073.common.util.ReflectionUtil.PrimitiveTypeGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Required so we can pass in a fake {@link SmartDashboard} during unit testing.
 */
public interface SmartDashboardAdapter {

    static SmartDashboardAdapter getInstance() {
        return SmartDashboardAdapterDefaultImpl.getInstance();
    }

    /**
     * Gets the table with the specified key.
     *
     * @param key the key name
     * @return The network table
     */
    NetworkTableAdapter getTable(String key);

    default void putValue(String key, Object value) {
        PrimitiveType primitiveType = ReflectionUtil.getPrimitiveType(value);
        PrimitiveTypeGroup type = primitiveType.getGroup();

        // TODO: Change this to check PrimitiveType instead of PrimitiveTypeGroup (we will get class cast exceptions otherwise)
        switch (type) {
            case TEXT:
                putString(key, (String) value);
                break;
            case BOOLEAN:
                putBoolean(key, (boolean) value);
                break;
            case DIGIT:
                if (primitiveType == PrimitiveType.BYTE) {
                    putNumber(key, ((Byte) value).doubleValue());
                } else if (primitiveType == PrimitiveType.INTEGER){
                    putNumber(key, ((Integer) value).doubleValue());
                } else if (primitiveType == PrimitiveType.LONG) {
                    putNumber(key, ((Long) value).doubleValue());
                }
                break;
            case DECIMAL:
                putNumber(key, (double) value);
                break;
            default:
                EnumUtil.throwUnknownValueException(type);
        }
    }

    void putBoolean(String key, boolean value);

    void putString(String key, String value);

    void putNumber(String key, double value);

}
