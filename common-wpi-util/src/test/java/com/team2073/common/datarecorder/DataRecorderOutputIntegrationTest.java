package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.SelfMutatingRecordable;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.FieldMapping;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author pbriggs
 */
class DataRecorderOutputIntegrationTest extends BaseDataRecorderTest  {

    @Test
    void VERIFY_OutputHandlerInitCalledOnlyOnce() {
        cleanRecorder.registerOutputHandler(mockedOutputHandler);
        cleanRecorder.registerRecordable(new BasicRecordable());
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualFlush().block();

        verify(mockedOutputHandler, times(1)).init();
    }

    @Test
    void VERIFY_DataOutputOnlyOnce() {
        cleanRecorder.registerOutputHandler(mockedOutputHandler);
        cleanRecorder.registerRecordable(new SelfMutatingRecordable());
        cleanRecorder.registerOutputHandler(cleanInMemOutputHandler);

        int totalIterations = runDataRecorderBlocking(cleanRecorder);

        Map<DataRecordTable, List<DataRecordRow>> inMemoryOutput = cleanInMemOutputHandler.getInMemoryOutput();
        assertThat(inMemoryOutput).size().isGreaterThanOrEqualTo(1);
        assertNoDuplicateRowsByTimestamp(inMemoryOutput, totalIterations);
        assertValuesAreUnique(cleanInMemOutputHandler, totalIterations);
    }

    @Test
    void VERIFY_OutputDataMatchesActualData() {
        cleanRecorder.registerRecordable(cleanHistoryAwareRecordable);
        cleanRecorder.registerOutputHandler(cleanInMemOutputHandler);
        runDataRecorderBlocking(cleanRecorder);

        Map<DataRecordTable, Map<FieldMapping<?>, List<String>>> inMemHistory = cleanInMemOutputHandler.getInMemoryOutputFlattened();
        assertThat(inMemHistory).size().isEqualTo(1);

        Entry<DataRecordTable, Map<FieldMapping<?>, List<String>>> tableEntry = inMemHistory.entrySet().iterator().next();
        assertThat(tableEntry.getKey().getRecordable()).isEqualTo(cleanHistoryAwareRecordable);

        Map<FieldMapping<?>, List<String>> fieldToValueMap = tableEntry.getValue();
        Set<Entry<FieldMapping<?>, List<String>>> fieldMap = fieldToValueMap.entrySet();
        assertThat(fieldMap).size().isEqualTo(1);

        Entry<FieldMapping<?>, List<String>> fieldMapEntry = fieldMap.iterator().next();
        List<String> recordedHistory = fieldMapEntry.getValue();
        List<String> actualHistory = cleanHistoryAwareRecordable.getHistory().stream().map(it -> String.valueOf(it)).collect(Collectors.toList());

        assertThat(recordedHistory).containsExactlyElementsOf(actualHistory);
    }

}