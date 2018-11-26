package com.team2073.common.datarecorder;

import com.team2073.common.config.CommonProperties;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.ComplexObjectFixture;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.HistoryAwareRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.InMemoryDataRecordOutputHandler;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.util.LogUtil;
import com.team2073.common.util.StreamUtil;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
@ExtendWith(MockitoExtension.class)
public class BaseDataRecorderTest extends BaseWpiTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    // 'Clean' fixtures (these have only been instantiated, not registered or anything)
    protected DataRecorder cleanRecorder;
    protected BasicRecordable cleanBasicRecordable;
    protected HistoryAwareRecordable cleanHistoryAwareRecordable;
    protected ComplexObjectFixture cleanComplexObject;
    protected InMemoryDataRecordOutputHandler cleanInMemOutputHandler;

    protected CommonProperties cleanProps;
    protected DataRecorderHelper cleanMapper;

    // 'Basic' grouping
    // Use initBasicDataRecorder() to get a handle on this DataRecorder
    private DataRecorder basicRecorder;
    private LifecycleAwareRecordable basicRecordable;
    private InMemoryDataRecordOutputHandler basicInMemOutputHandler;

    @Mock protected InMemoryDataRecordOutputHandler mockedOutputHandler;
    @Mock protected BasicRecordable mockedRecordable;

    @BeforeEach
    void baseDataRecorderInit() {
        log.info("");
        LogUtil.infoConstruct(this, log);
        log.info("");

        cleanRecorder = new DataRecorder();
        cleanBasicRecordable = new BasicRecordable();
        cleanHistoryAwareRecordable = new HistoryAwareRecordable();
        cleanComplexObject = new ComplexObjectFixture();
        cleanInMemOutputHandler = new InMemoryDataRecordOutputHandler();
        cleanMapper = new DataRecorderHelper();
        cleanProps = robotContext.getCommonProps();

        log.info("");
        LogUtil.infoConstructEnd(this, log);
        log.info("");
    }

    protected int runDataRecorderBlocking(DataRecorder recorder) {
        return runDataRecorderBlocking(recorder, 2, 30);
    }

    protected int runDataRecorderBlocking(DataRecorder recorder, int numbFlushes, int numbRecordsPerFlush) {

        int totalIterations = 0;

        for (int i = 0; i < numbFlushes; i++) {
            for (int j = 0; j < numbRecordsPerFlush; j++) {
                recorder.manualRecord().block();
                totalIterations++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    fail("Unexpected interruption.", e);
                }
            }
            recorder.manualFlush().block();
        }

        return totalIterations;
    }

    protected void assertNoDuplicateRowsByTimestamp(Map<DataRecordTable, List<DataRecordRow>> inMemoryOutput, int expectedIterations) {

        for (Entry<DataRecordTable, List<DataRecordRow>> entry : inMemoryOutput.entrySet()) {
            List<DataRecordRow> history = entry.getValue();
            assertThat(history).size().isEqualTo(expectedIterations);

            long count = history.stream().filter(StreamUtil.distinctByKey(it -> it.getTimeStamp())).count();
            assertThat(count).as("duplicate DataRecordRows").isEqualTo(expectedIterations);
        }

    }

    protected void assertValuesAreUnique(InMemoryDataRecordOutputHandler inMemoryOutputHandler, int expectedIterations) {

        Map<DataRecordTable, Map<FieldMapping<?>, List<String>>> inMemoryOutputFlattened = inMemoryOutputHandler.getInMemoryOutputFlattened();
        assertThat(inMemoryOutputFlattened.entrySet()).isNotEmpty();

        for (Entry<DataRecordTable, Map<FieldMapping<?>, List<String>>> entry : inMemoryOutputFlattened.entrySet()) {
            Map<FieldMapping<?>, List<String>> historyMap = entry.getValue();

            log.debug("==================== " + entry.getKey().getName());

            for (Entry<FieldMapping<?>, List<String>> historyMapEntry : historyMap.entrySet()) {
                List<String> history = historyMapEntry.getValue();
                String fieldName = historyMapEntry.getKey().getDataPointName();

                assertThat(history).size().isEqualTo(expectedIterations);

                log.debug("===== " + fieldName);
                history.forEach(it -> log.debug(it));
                log.debug("");

                long distinctCount = history.stream().distinct().count();
                assertThat(distinctCount)
                        .as("Field [%s] did not have at least two distinct values. All values were [%s].", fieldName, history.get(0))
                        .isGreaterThan(1);
            }
        }
    }

    public DataRecorder getAndInitBasicRecorder() {
        initBasicDataRecorder();
        return basicRecorder;
    }

    public LifecycleAwareRecordable getAndInitBasicRecordable() {
        initBasicDataRecorder();
        return basicRecordable;
    }

    public InMemoryDataRecordOutputHandler getAndInitBasicInMemOutputHandler() {
        initBasicDataRecorder();
        return basicInMemOutputHandler;
    }

    private void initBasicDataRecorder() {
        if (basicRecorder == null) {
            basicRecorder = new DataRecorder();
            basicRecordable = new BasicRecordable();
            basicInMemOutputHandler = new InMemoryDataRecordOutputHandler();
            basicRecorder.registerRecordable(basicRecordable);
            basicRecorder.registerOutputHandler(basicInMemOutputHandler);
        }
    }
}
