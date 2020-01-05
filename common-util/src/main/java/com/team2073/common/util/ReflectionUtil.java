package com.team2073.common.util;

import com.team2073.common.assertion.Assert;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


/**
 * @author pbriggs
 */
public abstract class ReflectionUtil {

    public enum PrimitiveType {
        BOOLEAN     (PrimitiveTypeGroup.BOOLEAN),
        CHARACTER   (PrimitiveTypeGroup.TEXT),
        SHORT       (PrimitiveTypeGroup.TEXT),
        STRING      (PrimitiveTypeGroup.TEXT),
        BYTE        (PrimitiveTypeGroup.DIGIT),
        INTEGER     (PrimitiveTypeGroup.DIGIT),
        LONG        (PrimitiveTypeGroup.DIGIT),
        FLOAT       (PrimitiveTypeGroup.DECIMAL),
        DOUBLE      (PrimitiveTypeGroup.DECIMAL);

        private final PrimitiveTypeGroup group;

        PrimitiveType(PrimitiveTypeGroup group) {
            this.group = group;
        }

        public PrimitiveTypeGroup getGroup() {
            return group;
        }
    }

    public enum PrimitiveTypeGroup {
        TEXT, DIGIT, DECIMAL, BOOLEAN
    }

    public static List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    result.add(field);
                }
            }
            i = i.getSuperclass();
        }

        return result;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type) || type.isAssignableFrom(String.class);
    }

    public static PrimitiveType getPrimitiveType(Object obj) {
        Assert.assertNotNull(obj, "obj");
        return getPrimitiveType(obj.getClass());
    }

    public static PrimitiveType getPrimitiveType(Field field) {
        return getPrimitiveType(field.getType());
    }

    public static PrimitiveType getPrimitiveType(Class<?> type) {
        return getPrimitiveType(type, true);
    }

    public static PrimitiveType getPrimitiveType(Class<?> type, boolean countStringAsPrimitive) {

        if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
            return PrimitiveType.DOUBLE;

        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
            return PrimitiveType.INTEGER;

        } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
            return PrimitiveType.LONG;

        } else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
            return PrimitiveType.FLOAT;

        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
            return PrimitiveType.BYTE;

        } else if (type.isAssignableFrom(Character.class) || type.isAssignableFrom(char.class)) {
            return PrimitiveType.CHARACTER;

        } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
            return PrimitiveType.SHORT;

        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
            return PrimitiveType.BOOLEAN;

        } else if (type.isAssignableFrom(String.class) || type.isAssignableFrom(String.class) && countStringAsPrimitive) {
            return PrimitiveType.STRING;
        }
        return null;
    }
    
    public static boolean isNestedStaticClass(Class<?> clazz) {
        return clazz.isMemberClass() && Modifier.isStatic(clazz.getModifiers());
    }
    
    public static boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }
}
