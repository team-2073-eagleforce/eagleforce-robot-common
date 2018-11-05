package com.team2073.common.datarecorder.output;

import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.RecordableRegistration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author pbriggs
 */
public class DataRecordOutputHandlerConsoleImpl implements DataRecordOutputHandler {

    @Override
    public void init() {

    }

    @Override
    public void flushTable(RecordableRegistration registration, DataRecordTable table) {
        String delimitter = "\t";
        System.out.println("============================================================");
        System.out.println(registration.getInstance().getClass().getSimpleName());
        System.out.println("============================================================");
        String data = "timestamp" + registration.getFieldMappingList()
                .stream()
                .map(e -> e.getDataPointName())
                .collect(Collectors.joining(delimitter, delimitter, "\n"));
        for (Entry<Long, DataRecordRow> row : table.entrySet()) {
            data += row.getKey() + delimitter;
            data = row.getValue()
                    .stream()
                    .map(e -> e.getFieldToString())
                    .collect(Collectors.joining(delimitter, data, "\n"));
        }
        System.out.println(data);
        System.out.println();
        System.out.println();
    }
}
