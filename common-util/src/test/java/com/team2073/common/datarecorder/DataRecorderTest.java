package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.model.RecordableRegistration;
import com.team2073.common.datarecorder.output.DataRecordOutputHandlerConsoleImpl;
import com.team2073.common.util.ThreadUtil;
import org.junit.jupiter.api.Test;

/**
 * @author pbriggs
 */
class DataRecorderTest {

    @Test
    public void recordableRegistrationTest() {
        RecordableRegistration reg = new RecordableRegistration(new BasicRecordable(), DataRecorder.DEFAULT_PERIOD);

        DataRecorder recorder = new DataRecorder();
        recorder.register(new BasicRecordable());
//        recorder.register(new BasicRecordable2());

        recorder.registerOutputHandler(new DataRecordOutputHandlerConsoleImpl());

        for (int i = 0; i < 30; i++) {
            recorder.record();
            ThreadUtil.sleep(100);
        }

        // assert sorted properly
        // test invalid characters in headers and rows
        // test invalid Recordable field type
        // Test different data types (float) converting into correct field mappings (double)

        recorder.flush();
    }

}