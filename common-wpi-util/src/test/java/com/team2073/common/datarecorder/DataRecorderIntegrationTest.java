package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.InMemoryDataRecordOutputHandler;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.NonMutatingRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.SelfMutatingRecordable;
import com.team2073.common.datarecorder.model.DataRecord;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerConsoleImpl;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerCsvImpl;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerSmartDashboardImpl;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.smartdashboard.adapter.SmartDashboardAdapterSimulationImpl;
import com.team2073.common.test.annon.TestNotWrittenYet;
import com.team2073.common.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static com.team2073.common.datarecorder.DataRecorderTestFixtures.SelfMutatingRecordable.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pbriggs
 */
class DataRecorderIntegrationTest extends BaseDataRecorderTest {

    @Test
    void fullIntegrationTest() {

        // 1-Create all your stuff
        DataRecordOutputHandlerConsoleImpl consoleOutput = new DataRecordOutputHandlerConsoleImpl();
        DataRecordOutputHandlerCsvImpl csvOutput = new DataRecordOutputHandlerCsvImpl();
        SmartDashboardAdapterSimulationImpl adapter = SmartDashboardAdapterSimulationImpl.getInstance();
        DataRecordOutputHandlerSmartDashboardImpl smartDashOutput = new DataRecordOutputHandlerSmartDashboardImpl(adapter);
        InMemoryDataRecordOutputHandler inMemOutput = new InMemoryDataRecordOutputHandler();

        // 2-Configure stuff
        // Optional (uses the user directory by default and prints the path to the console)
        csvOutput.setOutDir(new File(FileUtils.getTempDirectory(), "data-recorder-unit-tests"));

        // 3-Register
        SelfMutatingRecordable recordable1 = new SelfMutatingRecordable();
        NonMutatingRecordable recordable2 = new NonMutatingRecordable();
        cleanRecorder.registerRecordable(recordable1);
        cleanRecorder.registerRecordable(recordable2);

        cleanRecorder.registerOutputHandler(consoleOutput);
        cleanRecorder.registerOutputHandler(csvOutput);
        cleanRecorder.registerOutputHandler(smartDashOutput);
        cleanRecorder.registerOutputHandler(inMemOutput);

        int totalIterations = runDataRecorderBlocking(cleanRecorder);

        Collection<DataRecordTable> tableList = cleanRecorder.getDataTableList();
        assertThat(tableList).size().isGreaterThanOrEqualTo(2);

        Optional<DataRecordTable> table1Optional = tableList.stream().filter(it -> it.getRecordable().equals(recordable1)).findFirst();
        Optional<DataRecordTable> table2Optional = tableList.stream().filter(it -> it.getRecordable().equals(recordable2)).findFirst();

        assertTrue(table1Optional.isPresent());
        assertTrue(table2Optional.isPresent());

        DataRecordTable table1 = table1Optional.get();
        DataRecordTable table2 = table2Optional.get();

        List<FieldMapping> table1MappingList = table1.getFieldMappingList();
        List<FieldMapping> table2MappingList = table2.getFieldMappingList();

        assertThat(table1MappingList).isNotEmpty();
        assertThat(table2MappingList).isNotEmpty();
        assertThat(table1MappingList).isNotEqualTo(table2MappingList);

        assertThatIllegalStateException().isThrownBy(() -> table1.getRowsReadyToFlush());
        assertThatIllegalStateException().isThrownBy(() -> table2.getRowsReadyToFlush());

        Map<DataRecordTable, List<DataRecordRow>> inMemoryOutput = inMemOutput.getInMemoryOutput();
        assertThat(inMemoryOutput).size().isGreaterThanOrEqualTo(2);

        assertNoDuplicateRowsByTimestamp(inMemoryOutput, totalIterations);

        Optional<Entry<DataRecordTable, List<DataRecordRow>>> entryOptional = inMemoryOutput.entrySet().stream().filter(it -> it.getKey().getRecordable().equals(recordable1)).findFirst();
        assertTrue(entryOptional.isPresent());

        List<String> fields = new ArrayList<>();
        Entry<DataRecordTable, List<DataRecordRow>> entry = entryOptional.get();
        for (DataRecordRow row : entry.getValue()) {
            for (DataRecord<?> record : row.getDataPointList()) {
                if (record.getFieldName().equals(ITERATIONS_FIELD_NAME)) {
                    fields.add(record.getFieldToString());
                }
            }
        }

        assertThat(fields).size().isEqualTo(totalIterations);
        assertThat(fields).doesNotHaveDuplicates();

        // Optionally print file names to console
        System.out.println("Root directory: " + csvOutput.getOutFileMap().entrySet().iterator().next().getValue().getParentFile().getAbsolutePath());

        for (Entry<String, File> e : csvOutput.getOutFileMap().entrySet()) {
            System.out.println("\t File: " + e.getValue().getParentFile().getAbsolutePath());
        }
    }

    @Test
    @TestNotWrittenYet
    void periodicRunnerIntegrationTest() {
        // run from periodic runner
        long flushInterval = 1000L;
        robotContext.getCommonProps().setDataRecorderAutoFlushInterval(flushInterval);
        DataRecorder recorder = getAndInitBasicRecorder();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();
        PeriodicRunner periodicRunner = robotContext.getPeriodicRunner();
        recorder.registerWithPeriodicRunner(periodicRunner);

        long totalTime = flushInterval * 2;
        int sleepTime = 10;
        long numbIterations = totalTime / sleepTime;

        for (int i = 0; i < numbIterations; i++) {
            periodicRunner.invokePeriodicInstances();
            ThreadUtil.sleep(sleepTime);
        }

//        left off here // Find out how to get the last remaining records from the queue


        System.out.println(System.currentTimeMillis());
        recorder.requestShutdownAndWait();
        System.out.println(System.currentTimeMillis());

        assertThat(output.getNumberOfRows()).isEqualTo(numbIterations);
    }

}