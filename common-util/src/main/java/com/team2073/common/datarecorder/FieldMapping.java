package com.team2073.common.datarecorder;

import com.team2073.common.util.ExceptionUtil;

import java.lang.reflect.Field;

/**
 * @author pbriggs
 */
abstract class FieldMapping<T> {

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
            ExceptionUtil.illegalState("Exception occurred attempting to access field [%s] value from class " +
                    "[%s]. Exception: ", e, field.getName(), instance.getClass().getSimpleName());

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
}

class StringFieldMapping extends FieldMapping<String> {
    public StringFieldMapping(Field field, String dataPointName, Recordable instance) {
        super(field, dataPointName, instance);
    }
}

class DoubleFieldMapping extends FieldMapping<Double> {
    public DoubleFieldMapping(Field field, String dataPointName, Recordable instance) {
        super(field, dataPointName, instance);
    }
}

class LongFieldMapping extends FieldMapping<Long> {
    public LongFieldMapping(Field field, String dataPointName, Recordable instance) {
        super(field, dataPointName, instance);
    }
}

class BooleanFieldMapping extends FieldMapping<Boolean> {
    public BooleanFieldMapping(Field field, String dataPointName, Recordable instance) {
        super(field, dataPointName, instance);
    }
}
