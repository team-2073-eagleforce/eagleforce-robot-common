package com.team2073.common.datarecorder.output;

import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pbriggs
 */
public class DataRecordOutputHandlerConsoleImpl implements DataRecordOutputHandler {

    // TODO: Extract to props
    private final String delimitter = "\t";

    @Override
    public void init() {

    }

    @Override
    public void flushTable(DataRecordTable registration, List<DataRecordRow> rowList) {
        System.out.println("============================================================");
        System.out.println(registration.getName());
        System.out.println("============================================================");
        String data = "timestamp" + registration.getFieldMappingList()
                .stream()
                .map(e -> e.getDataPointName())
                .collect(Collectors.joining(delimitter, delimitter, "\n"));
        for (DataRecordRow row : rowList) {
            data += row.formatTimestamp() + delimitter;
            data = row.stream()
                    .map(e -> e.getFieldToString())
                    .collect(Collectors.joining(delimitter, data, "\n"));
        }
        System.out.println(data);
        System.out.println();
        System.out.println();
    }
}
