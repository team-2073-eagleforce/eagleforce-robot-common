package com.team2073.common.datarecorder.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pbriggs
 */
public class RecordableRegistration {
    private Logger log = LoggerFactory.getLogger(getClass());

    private final Recordable instance;
    private final long period;
    private final List<FieldMapping> fieldMappingList = new ArrayList<>();

    public RecordableRegistration(Recordable instance, long period) {
        this.instance = instance;
        this.period = period;
    }

    public Recordable getInstance() {
        return instance;
    }

    public long getPeriod() {
        return period;
    }

    public List<FieldMapping> getFieldMappingList() {
        return fieldMappingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        // Ignore period when calculating equality (so when adding to map, we don't register the same
        // instance multiple times even with a different interval

        RecordableRegistration that = (RecordableRegistration) o;

        return new EqualsBuilder()
                .append(instance, that.instance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(instance)
                .toHashCode();
    }
}
