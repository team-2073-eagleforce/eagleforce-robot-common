package com.team2073.common.datarecorder;

import com.team2073.common.exception.NotYetImplementedException;
import com.team2073.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author pbriggs
 */
public class DataRecorder {

    public enum State {
        NEW,
        REGISTERING,
        AUTO_RECORDING,
        MANUAL_RECORDING;
    }

    public static long DEFAULT_PERIOD = 20;

    private Logger log = LoggerFactory.getLogger(getClass());
    private State state = State.NEW;
//    private List<RecordableRegistration> recordableList = new ArrayList<>();
    private Map<RecordableRegistration, Map<LocalDateTime, List<DataRecord<?>>>> dataRecordMap = new HashMap<>();

    public void register(Recordable recordable) {
        register(recordable, DEFAULT_PERIOD);
    }

    public void register(Recordable recordable, long period) {
        if (started())
            ExceptionUtil.illegalState("Cannot register any more instances when state is already [%s].", state);

//        recordableList.add(new RecordableRegistration(recordable, period));
        Map<LocalDateTime, List<DataRecord<?>>> table = dataRecordMap
                .computeIfAbsent(new RecordableRegistration(recordable, period), k -> new TreeMap<>());
    }

    public void startRecording() {
        if (manualRecording())
            ExceptionUtil.illegalState("Attempted to start auto recording when manual recording has already started.");

        // TODO: Start thread and call recordInternal()
        throw new NotYetImplementedException();
    }

    /** Call this method periodically. This will poll all the instances and record their data. Alternativally
     * call {@link #startRecording()} one time to handle this automatically in a different thread. */
    public void record() {
        if (manualRecording())
            ExceptionUtil.illegalState("Attempted to manual record when auto recording has already started.");

        recordInternal();
    }

    public void flush() {
//        Set<Entry<LocalDateTime, List<DataRecord<?>>>> entrySet = dataRecordMap.entrySet();
        Set<Entry<RecordableRegistration, Map<LocalDateTime, List<DataRecord<?>>>>> entrySet = dataRecordMap.entrySet();
        String delimitter = "\t";

        for (Entry<RecordableRegistration, Map<LocalDateTime, List<DataRecord<?>>>> table : entrySet) {
            System.out.println("============================================================");
            System.out.println(table.getKey().getInstance().getClass().getSimpleName());
            System.out.println("============================================================");
            String data = "timestamp" + table.getKey().getFieldMappingList()
                    .stream()
                    .map(e -> e.getDataPointName())
                    .collect(Collectors.joining(delimitter, delimitter, "\n"));
            for (Entry<LocalDateTime, List<DataRecord<?>>> row : table.getValue().entrySet()) {
                data += row.getKey().toEpochSecond(ZoneOffset.UTC) + delimitter;
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

    private void recordInternal() {
        LocalDateTime timeStamp = LocalDateTime.now();
        List<RecordableRegistration> tableList = dataRecordMap.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
        for (RecordableRegistration recordable : tableList) {
            recordable.getInstance().onBeforeRecord();
            Map<LocalDateTime, List<DataRecord<?>>> table = dataRecordMap.get(recordable);

            List<DataRecord<?>> dataRecordList = new ArrayList<>();
            table.put(timeStamp, dataRecordList);
            for (FieldMapping mapping : recordable.getFieldMappingList()) {
                dataRecordList.add(new DataRecord<>(mapping.getDataPointName(), mapping.getFieldValue(), mapping));
            }
        }
    }

    private boolean started() {
        return !state.equals(State.NEW) && !state.equals(State.REGISTERING);
    }

    private boolean autoRecording() {
        return state.equals(State.AUTO_RECORDING);
    }

    private boolean manualRecording() {
        return state.equals(State.MANUAL_RECORDING);
    }

}
