package com.team2073.common.datarecorder.model;

import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author pbriggs
 */
public class DataRecordTable {

    public enum TimestampFormat {
        LOCAL_DATE_TIME,
        MILLISECONDS,
        MILLISECONDS_SINCE_BOOT,
        CUSTOM;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    // Meta
    private final RecordableWrapper recordable;
    private final String name;
    private final long period;
    private final LocalDateTime initTimestamp;
    private final TimestampFormat timestampFormat;
    private final DateTimeFormatter customFormatter;
    private final List<FieldMapping> fieldMappingList;

    // Data
    private final Queue<DataRecordRow> rowQueue = new ConcurrentLinkedQueue<>();
    private List<DataRecordRow> readyToFlush;

    public DataRecordTable(RecordableWrapper recordable,
                           List<FieldMapping> fieldMappingList,
                           String name,
                           long period,
                           LocalDateTime initTimestamp,
                           TimestampFormat timestampFormat,
                           Optional<String> customTimestampFormat) {

        this.recordable = recordable;
        this.fieldMappingList = fieldMappingList;
        this.name = name;
        this.period = period;
        this.initTimestamp = initTimestamp;
        this.timestampFormat = timestampFormat;
        if (timestampFormat == TimestampFormat.CUSTOM) {
            if (!customTimestampFormat.isPresent())
                Throw.illegalState("If [{}] is set to [{}], a custom timestamp must be provided.");
            customFormatter = DateTimeFormatter.ofPattern(customTimestampFormat.get());
        } else {
            customFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
    }

    public void record() {
        LocalDateTime timeStamp = LocalDateTime.now();

        recordable.onBeforeRecord();

        List<DataRecord<?>> cellList = new ArrayList<>();
        for (FieldMapping mapping : fieldMappingList) {
            cellList.add(new DataRecord<>(mapping.getDataPointName(), mapping.getFieldValue(), mapping));
        }
        DataRecordRow row = new DataRecordRow(this, timeStamp, cellList);

        recordable.onAfterRecord();

        rowQueue.add(row);
    }

    public void onBeforeFlush() {
        readyToFlush = new ArrayList<>();
        DataRecordRow poll;
        while ((poll = rowQueue.poll()) != null) {
            readyToFlush.add(poll);
        }
    }

    public void onAfterFlush() {
        readyToFlush = null;
    }

    public String convertTimestampToOutput(LocalDateTime timestamp) {
        switch (timestampFormat) {

            case LOCAL_DATE_TIME:
                return timestamp.format(customFormatter);

            case MILLISECONDS:
                return String.valueOf(timestamp.toEpochSecond(ZoneOffset.UTC));

            case MILLISECONDS_SINCE_BOOT:
                return String.valueOf(Duration.between(initTimestamp, timestamp).toMillis());

            case CUSTOM:
                return timestamp.format(customFormatter);

            default:
                EnumUtil.throwUnknownValueException(timestampFormat);
                // dead code
                return null;
        }
    }

    public List<DataRecordRow> getRowsReadyToFlush() {
        if (readyToFlush == null)
            Throw.illegalState("Cannot access records ready to flush before onBeforeFlush() or after onAfterFlush().");
        return Collections.unmodifiableList(readyToFlush);
    }

    public long getPeriod() {
        return period;
    }

    public String getName() {
        return name;
    }

    public Object getRecordable() {
        return recordable.getInstance();
    }

    public List<FieldMapping> getFieldMappingList() {
        return Collections.unmodifiableList(fieldMappingList);
    }

}
