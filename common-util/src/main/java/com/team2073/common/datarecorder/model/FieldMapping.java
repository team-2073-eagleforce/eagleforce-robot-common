package com.team2073.common.datarecorder.model;

import com.team2073.common.util.Throw;

import java.lang.reflect.Field;

/**
 * @author pbriggs
 */
public abstract class FieldMapping<T> {

    private final Field field;
    private final String dataPointName;
    private final Recordable instance;

    public FieldMapping(Field field, String dataPointName, Recordable instance) {
        this.field = field;
        this.dataPointName = dataPointName;
        this.instance = instance;
    }

    public T getFieldValue() {
        try {
            return (T) getField().get(getInstance());
        } catch (IllegalAccessException e) {
            Throw.illegalState("Exception occurred attempting to access field [%s] value from class " +
                    "[%s]. Exception: ", field.getName(), instance.getClass().getSimpleName(), e);

            // dead code
            return null;
        }
    }

    public String fieldToString() {
        return String.valueOf(getFieldValue());
    }

    public Field getField() {
        return field;
    }

    public String getDataPointName() {
        return dataPointName;
    }

    public Recordable getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return fieldToString();
    }

    public static class StringFieldMapping extends FieldMapping<String> {
        public StringFieldMapping(Field field, String dataPointName, Recordable instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class DoubleFieldMapping extends FieldMapping<Double> {
        public DoubleFieldMapping(Field field, String dataPointName, Recordable instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class LongFieldMapping extends FieldMapping<Long> {
        public LongFieldMapping(Field field, String dataPointName, Recordable instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class BooleanFieldMapping extends FieldMapping<Boolean> {
        public BooleanFieldMapping(Field field, String dataPointName, Recordable instance) {
            super(field, dataPointName, instance);
        }
    }
}
