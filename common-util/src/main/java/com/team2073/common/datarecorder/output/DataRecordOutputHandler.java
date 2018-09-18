package com.team2073.common.datarecorder.output;

import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.RecordableRegistration;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author pbriggs
 */
public interface DataRecordOutputHandler {

    default void flushAllTables(Map<RecordableRegistration, DataRecordTable> dataRecordMap) {
        for (Entry<RecordableRegistration, DataRecordTable> table : dataRecordMap.entrySet()) {
            flushTable(table.getKey(), table.getValue());
        }
    }

    void flushTable(RecordableRegistration registration, DataRecordTable table);

}
