package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable2;
import com.team2073.common.datarecorder.model.Recordable;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerConsoleImpl;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerCsvImpl;
import com.team2073.common.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map.Entry;

/**
 * @author pbriggs
 */
class DataRecorderTest {

    @Test
    public void recordableRegistrationTest() {

        // 1-Create all your stuff
        DataRecorder recorder = new DataRecorder();
        DataRecordOutputHandlerConsoleImpl consoleOutput = new DataRecordOutputHandlerConsoleImpl();
        DataRecordOutputHandlerCsvImpl csvOutput = new DataRecordOutputHandlerCsvImpl();

        // 2-Configure stuff
        // Optional (uses temp directory by default and prints the path to the console)
        csvOutput.setOutDir(new File(FileUtils.getUserDirectory(), "data-recorder"));

        // 3-Register
        recorder.register(new BasicRecordable());
        recorder.register(new BasicRecordable2());

        recorder.registerOutputHandler(consoleOutput);
        recorder.registerOutputHandler(csvOutput);

        // 4-Run
        for (int i = 0; i < 30; i++) {
            recorder.record();
            ThreadUtil.sleep(100);
        }

        // 5-Flush (this writes to the files)
        recorder.flush();

        // Optionally repeat
        for (int i = 0; i < 30; i++) {
            recorder.record();
            ThreadUtil.sleep(100);
        }

        recorder.flush();

        // Optionally print file names to console
        System.out.println("Root directory: " + csvOutput.getOutFileMap().entrySet().iterator().next().getValue().getParentFile().getAbsolutePath());

        for (Entry<Recordable, File> entry : csvOutput.getOutFileMap().entrySet()) {
            System.out.println("\t File: " + entry.getValue().getParentFile().getAbsolutePath());
        }

        // TODO
        // assert sorted properly
        // test invalid characters in headers and rows
        // test invalid Recordable field type
        // Test different data types (float) converting into correct field mappings (double)
        // no instances registered
        // no output handlers registered
        // Test invalid output dir
        // Test forbidden file locations
    }

}