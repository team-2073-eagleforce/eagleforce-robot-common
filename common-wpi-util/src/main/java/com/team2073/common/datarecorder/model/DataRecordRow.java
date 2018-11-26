package com.team2073.common.datarecorder.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author pbriggs
 */
public class DataRecordRow {

    private final DataRecordTable dataTable;
    private final LocalDateTime timeStamp;
    private final List<DataRecord<?>> dataPointList;

    public DataRecordRow(DataRecordTable dataTable, LocalDateTime timeStamp, List<DataRecord<?>> dataPointList) {
        this.dataTable = dataTable;
        this.timeStamp = timeStamp;
        this.dataPointList = dataPointList;
    }

    public List<DataRecord<?>> getDataPointList() {
        return Collections.unmodifiableList(dataPointList);
    }

    public String formatTimestamp() {
        return dataTable.convertTimestampToOutput(timeStamp);
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public Stream<DataRecord<?>> stream() {
        return getDataPointList().stream();
    }
}
