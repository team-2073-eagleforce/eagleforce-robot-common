package com.team2073.common.util;

import com.team2073.common.assertion.Assert;

/**
 * @author pbriggs
 */
public abstract class ClassUtil {

    public static String simpleName(Object object) {
        Assert.assertNotNull(object, "object");
        return object.getClass().getSimpleName();
    }

    /** Safe version of {@link #simpleName(Object)} (allows null values). */
    public static String simpleNameSafe(Object object) {
        if (object == null) {
            return "null";
        } else {
            return object.getClass().getSimpleName();
        }
    }

}
