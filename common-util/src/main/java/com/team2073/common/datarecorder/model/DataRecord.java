package com.team2073.common.datarecorder.model;

/**
 *
 *
 * @author pbriggs
 */
public class DataRecord<T> {

    private final String fieldName;
    private final T fieldValue;
    private final FieldMapping<T> mapping;

    public DataRecord(String fieldName, T fieldValue, FieldMapping<T> mapping) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.mapping = mapping;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldToString() {
        return String.valueOf(fieldValue);
    }

    public T getFieldValue() {
        return fieldValue;
    }

    public FieldMapping getMapping() {
        return mapping;
    }
}
