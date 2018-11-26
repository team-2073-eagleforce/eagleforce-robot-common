package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.model.DataPoint;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.DataRecord;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.EnumDataPoint;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.datarecorder.output.DataRecordOutputHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

/**
 * @author pbriggs
 */
public abstract class DataRecorderTestFixtures {

    public enum  SimpleEnum {
        FIELD_ONE, FIELD_TWO, FIELD_THREE;
    }

    public static final Long CONTROL_DATA = 5L;

    public enum CustomDataPointEnum implements EnumDataPoint { // <CustomDataPointEnum> {
        ONE(4L),
        TWO(CONTROL_DATA),
        THREE(6L);

        private final Long value;

        CustomDataPointEnum(Long value) {
            this.value = value;
        }

        @Override
        public Long convertToDataPoint() {
            return value;
        }
    }

    public static class ComplexObjectFixture {
        public static final String[] FIELD_NAMES = {"booleanInnerField", "charInnerField", "byteInnerField", "complexInnerField", };
        private boolean booleanInnerField = true;
        private char charInnerField = 'a';
        private byte byteInnerField = 1;
        private File complexInnerField = new File("");
    }

    public static class BasicRecordable implements LifecycleAwareRecordable {
        private int a = 1;
    }

    /** A {@link LifecycleAwareRecordable} whose values automatically change to simulate a real object. */
    public static class SelfMutatingRecordable implements LifecycleAwareRecordable {

        private String state = "INITIALIZING";

        public static final String ITERATIONS_FIELD_NAME = "#";
        @DataPoint(name = ITERATIONS_FIELD_NAME)
        private int iterations = 0;
        private double position = 2.34;
        private boolean active = true;

        @Override
        public void onBeforeRecord() {
            iterations++;
            position += (1 * new Random().nextDouble());
            active = new Random().nextBoolean();
            if (iterations < 2)
                state = "INITIALIZING";
            else if (iterations < 5)
                state = "WAITING";
            else if (iterations < 15)
                state = "MOVING";
            else if (iterations < 20)
                state = "WAITING";
            else if (iterations < 28)
                state = "MOVING";
            else
                state = "WAITING";
        }
    }

    /** A {@link LifecycleAwareRecordable} whose values do not change. */
    public static class NonMutatingRecordable implements LifecycleAwareRecordable {

        private String stringVar = "some string";
        private int intVar = 2;
        private double doubleVar = 2.22;
        private long longVar = 222;
        private boolean booleanVar = true;
    }

    public static class HistoryAwareRecordable implements LifecycleAwareRecordable {
        private int foo = 2;

        @DataPointIgnore
        private List<Integer> history = new ArrayList<>();

        @Override
        public void onBeforeRecord() {
            foo += 4;
            history.add(foo);
        }

        public List<Integer> getHistory() {
            return Collections.unmodifiableList(history);
        }
    }

    public static class InMemoryDataRecordOutputHandler implements DataRecordOutputHandler {

        private Map<DataRecordTable, List<DataRecordRow>> inMemoryOutput = new HashMap<>();
        private Map<DataRecordTable, Map<FieldMapping<?>, List<String>>> inMemoryOutputFlattened = new HashMap<>();

        @Override
        public void init() {

        }

        @Override
        public void flushTable(DataRecordTable table, List<DataRecordRow> rowList) {
            inMemoryOutput.computeIfAbsent(table, reg -> new ArrayList<>()).addAll(rowList);
            Map<FieldMapping<?>, List<String>> fieldMappingListMap = inMemoryOutputFlattened.computeIfAbsent(table, e -> new HashMap<>());
            mergeHistoryOfTable(fieldMappingListMap, rowList);
        }

        public Map<DataRecordTable, List<DataRecordRow>> getInMemoryOutput() {
            return Collections.unmodifiableMap(inMemoryOutput);
        }

        public Map<DataRecordTable, Map<FieldMapping<?>, List<String>>> getInMemoryOutputFlattened() {
            return Collections.unmodifiableMap(inMemoryOutputFlattened);
        }

        public int getNumberOfRows() {
            Map<DataRecordTable, Map<FieldMapping<?>, List<String>>> inMemoryOutputMap = getInMemoryOutputFlattened();
            assertThat(inMemoryOutputMap).isNotEmpty();

            Map<FieldMapping<?>, List<String>> fieldRowMap = inMemoryOutputMap.entrySet().iterator().next().getValue();
            assertThat(fieldRowMap).isNotEmpty();

            return fieldRowMap.entrySet().iterator().next().getValue().size();
        }

        public static Map<FieldMapping<?>, List<String>> getHistoryOfTable(List<DataRecordRow> history) {
            Map<FieldMapping<?>, List<String>> valuesByField = new HashMap<>();
            mergeHistoryOfTable(valuesByField, history);
            return valuesByField;
        }

        public static Map<FieldMapping<?>, List<String>> mergeHistoryOfTable(Map<FieldMapping<?>, List<String>> existingHistory, List<DataRecordRow> history) {
            for (DataRecordRow row : history) {
                for (DataRecord<?> record : row.getDataPointList()) {
                    List<String> historyDto = existingHistory.computeIfAbsent(record.getMapping(), key -> new ArrayList<>());
                    historyDto.add(record.getFieldToString());
                }
            }

            return existingHistory;
        }
    }

}
