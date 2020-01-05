package com.team2073.common.datarecorder.model;

import com.google.common.base.Objects;

/**
 * @author Preston Briggs
 */
public abstract class RecordableWrapper {

    public abstract void onBeforeRecord();

    public abstract void onAfterRecord();

    public abstract Object getInstance();

    @Override
    public boolean equals(Object o) {
        // Needed for use in DataRecorder's Map<RecordableWrapper, DataRecordTable> dataTableMap
        if (this == o) return true;
        if (!(o instanceof RecordableWrapper)) return false;
        RecordableWrapper that = (RecordableWrapper) o;
        return Objects.equal(getInstance(), that.getInstance());
    }

    @Override
    public int hashCode() {
        // Needed for use in DataRecorder's Map<RecordableWrapper, DataRecordTable> dataTableMap
        return Objects.hashCode(getInstance());
    }
}