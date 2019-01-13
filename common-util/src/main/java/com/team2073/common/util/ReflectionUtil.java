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

    public static void main(String[] args) {

        // TODO: turn this into a test

        PrimitiveType type;
        type = getPrimitiveType(Double.class);
        System.out.println("Double: " + type);

        type = getPrimitiveType(Integer.class);
        System.out.println("Integer: " + type);

        type = getPrimitiveType(Long.class);
        System.out.println("Long: " + type);

        type = getPrimitiveType(Float.class);
        System.out.println("Float: " + type);

        type = getPrimitiveType(Byte.class);
        System.out.println("Byte: " + type);

        type = getPrimitiveType(Character.class);
        System.out.println("Character: " + type);

        type = getPrimitiveType(Short.class);
        System.out.println("Short: " + type);

        type = getPrimitiveType(String.class);
        System.out.println("String: " + type);

        type = getPrimitiveType(Boolean.class);
        System.out.println("Boolean: " + type);


        type = getPrimitiveType(double.class);
        System.out.println("Double: " + type);

        type = getPrimitiveType(int.class);
        System.out.println("int: " + type);

        type = getPrimitiveType(long.class);
        System.out.println("long: " + type);

        type = getPrimitiveType(float.class);
        System.out.println("float: " + type);

        type = getPrimitiveType(byte.class);
        System.out.println("byte: " + type);

        type = getPrimitiveType(char.class);
        System.out.println("char: " + type);

        type = getPrimitiveType(short.class);
        System.out.println("short: " + type);

        type = getPrimitiveType(String.class);
        System.out.println("String: " + type);

        type = getPrimitiveType(boolean.class);
        System.out.println("boolean: " + type);
    }

    public static PrimitiveType getPrimitiveType(Object obj) {
        Assert.assertNotNull(obj, "obj");
        return getPrimitiveType(obj.getClass());
    }

    public static PrimitiveType getPrimitiveType(Field field) {
        return getPrimitiveType(field.getType());
    }

    public static PrimitiveType getPrimitiveType(Class<?> type) {

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

        } else if (type.isAssignableFrom(String.class) || type.isAssignableFrom(String.class)) {
            return PrimitiveType.STRING;

        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
            return PrimitiveType.BOOLEAN;
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
