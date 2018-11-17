package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.InMemoryDataRecordOutputHandler;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.test.annon.TestNotWrittenYet;
import com.team2073.common.util.ThreadUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Preston Briggs
 */
@ExtendWith(MockitoExtension.class)
public class DataRecorderRecordingTest extends BaseDataRecorderTest {

    @Test
    void autoRecording() {
        cleanProps.setDataRecorderAutoFlushInterval(1000L);
        cleanRecorder.registerRecordable(mockedRecordable);
        cleanRecorder.registerOutputHandler(mockedOutputHandler);

        cleanRecorder.startAutoRecordAndFlush();
        ThreadUtil.sleep(cleanProps.getDataRecorderAutoFlushInterval() * 2);

        verify(mockedRecordable, atLeastOnce()).onBeforeRecord();
        verify(mockedOutputHandler, times(1)).init();
        verify(mockedOutputHandler, atLeastOnce()).flushTable(any(), any());
    }

    @Test
    @TestNotWrittenYet
    void manualRecording() {

        // Not important, we can add later

    }

    @Test
    @DisplayName("GIVEN: Auto recording already started - WHEN: Attempting to manual record - THEN: IllegalStateException is thrown")
    void manualRecordingAfterAutoRecordingStarted() {
        cleanRecorder.registerRecordable(cleanBasicRecordable);
        cleanRecorder.registerConsoleOutputHandler();
        cleanRecorder.startAutoRecordAndFlush();
        assertThatIllegalStateException().isThrownBy(() -> cleanRecorder.manualRecord().block());
    }

    @Mock private LifecycleAwareRecordable mockedRecordable = new BasicRecordable();

    @Test
    void recordableCalledCorrectNumberOfTimes() {
        cleanRecorder.registerRecordable(mockedRecordable);
        cleanRecorder.registerOutputHandler(new InMemoryDataRecordOutputHandler());
        int numbIterations = runDataRecorderBlocking(cleanRecorder);
        verify(mockedRecordable, times(numbIterations)).onBeforeRecord();
    }
}
