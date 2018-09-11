package com.team2073.common.datarecorder;

/**
 * @author pbriggs
 */
class DataRecord<T> {

    private final String fieldName;
    private final T fieldValue;
//    private final RecordableRegistration registration;
    private final FieldMapping mapping;

    public DataRecord(String fieldName, T fieldValue, FieldMapping mapping) {
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
