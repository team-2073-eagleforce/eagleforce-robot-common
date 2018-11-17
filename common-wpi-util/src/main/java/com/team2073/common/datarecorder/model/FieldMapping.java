package com.team2073.common.datarecorder.model;

import com.team2073.common.util.Throw;

import java.lang.reflect.Field;

/**
 * @author pbriggs
 */
public abstract class FieldMapping<T> {

    private final Field field;
    private final String dataPointName;
    private final Object instance;

    public FieldMapping(Field field, String dataPointName, Object instance) {
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

    public String fieldToString(T value) {
        return String.valueOf(value);
    }

    // TODO: This always returns the CURRENT value so when it gets ran during flush it returns the last value for
    // all of the mappings. We should find out why it was ever created and get rid of it if not needed
    public String fieldToString() {
        return fieldToString(getFieldValue());
    }

    public Field getField() {
        return field;
    }

    public String getDataPointName() {
        return dataPointName;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return fieldToString();
    }

    public static class StringFieldMapping extends FieldMapping<String> {
        public StringFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class DoubleFieldMapping extends FieldMapping<Double> {
        public DoubleFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class LongFieldMapping extends FieldMapping<Long> {
        public LongFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class BooleanFieldMapping extends FieldMapping<Boolean> {
        public BooleanFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }

        @Override
        public String fieldToString(Boolean value) {
            return value ? String.valueOf(1) : String.valueOf(0);
        }
    }

    public static class EnumFieldMapping extends FieldMapping<Enum> {
        public EnumFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }
    }

    public static class EnumDataPointFieldMapping extends FieldMapping<EnumDataPoint> {
        public EnumDataPointFieldMapping(Field field, String dataPointName, Object instance) {
            super(field, dataPointName, instance);
        }

        @Override
        public String fieldToString(EnumDataPoint value) {
            return String.valueOf(value.convertToDataPoint());
        }
    }
}
