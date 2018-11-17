package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.InMemoryDataRecordOutputHandler;
import com.team2073.common.test.annon.TestNotWrittenYet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Preston Briggs
 */
public class DataRecorderDisabledTest extends BaseDataRecorderTest {

    @Test
    @DisplayName("WHEN: No periodic instances registered - THEN: Don't run DataRecorder")
    void noPeriodicInstances() {
        cleanRecorder.registerOutputHandler(mockedOutputHandler);
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualFlush().block();

        verify(mockedOutputHandler, never()).init();
        verify(mockedOutputHandler, never()).flushTable(any(), any());
        verify(mockedOutputHandler, never()).init();
    }

    @Test
    @DisplayName("WHEN: No periodic or outputhandler instances registered - THEN: Don't throw exception")
    void noPeriodicOrOutputHandlerInstances() {
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualRecord().block();
        cleanRecorder.manualFlush().block();
    }

    @Test
    @DisplayName("WHEN: realMatch property is true - THEN: Don't run DataRecorder")
    void realMatchProperty() {
        robotContext.getCommonProps().setRealMatch(true);
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();
        runDataRecorderBlocking(basicRecorder);
        assertThat(output.getInMemoryOutput()).isEmpty();
    }

    @Test
    @DisplayName("WHEN: dataRecorderEnabled property is false - THEN: Don't run DataRecorder")
    void dataRecorderEnabledProperty() {
        robotContext.getCommonProps().setDataRecorderEnabled(false);
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();
        runDataRecorderBlocking(basicRecorder);
        assertThat(output.getInMemoryOutput()).isEmpty();
    }

    @Test
    @DisplayName("WHEN: DataRecorder is manually disabled - THEN: Don't run DataRecorder")
    void dataRecorderManuallyDisabled() {
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        basicRecorder.disable();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();
        runDataRecorderBlocking(basicRecorder);
        assertThat(output.getInMemoryOutput()).isEmpty();
    }

    @Test
    @DisplayName("GIVEN: Manual recording already started - WHEN: Disabling manually - THEN: DataRecorder is cleanly shutdown, files are closed properly, and stop recording/flushing")
    void manuallyRecordThenDisable() {
        // Setup
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();

        // Run
        int enabledIterations = runDataRecorderBlocking(basicRecorder);
        basicRecorder.disable();
        int disabledIterations = runDataRecorderBlocking(basicRecorder, 2, 10);

        // Assert
        assertThat(output.getNumberOfRows()).isEqualTo(enabledIterations);
        assertThat(output.getNumberOfRows()).isNotEqualTo(disabledIterations);
    }

    @Test
    @TestNotWrittenYet
    @DisplayName("GIVEN: Auto recording already started - WHEN: Disabling manually - THEN: DataRecorder is cleanly shutdown, files are closed properly, and stop recording/flushing")
    void autoRecordThenDisable() {

    }

    @Test
    @DisplayName("GIVEN: Previously manually disabled - WHEN: Manually enabled - THEN: DataRecorder is not disabled and runs properly")
    void disableManuallyThenEnableManually() {
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        basicRecorder.disable();
        InMemoryDataRecordOutputHandler output = getAndInitBasicInMemOutputHandler();
        int disabledIterations = runDataRecorderBlocking(basicRecorder, 2, 10);
        assertThat(output.getInMemoryOutput()).isEmpty();

        basicRecorder.enable();
        int enabledIterations = runDataRecorderBlocking(basicRecorder);

        int actualIterations = output.getNumberOfRows();
        assertThat(actualIterations).isEqualTo(enabledIterations);
        assertThat(actualIterations).isNotEqualTo(disabledIterations);
    }

    @Test
    @TestNotWrittenYet
    @DisplayName("GIVEN: Previously disabled by properties - WHEN: Enabled by properties - THEN: DataRecorder is not disabled and runs properly")
    void disableByPropsThenEnableByProps() {

    }

    @Test
    @TestNotWrittenYet
    @DisplayName("GIVEN: Previously disabled and auto then auto-recording started - WHEN: Enabled - THEN: DataRecorder is not disabled and auto-records properly")
    void disableThenAutoRecordThenEnable() {

    }
}
