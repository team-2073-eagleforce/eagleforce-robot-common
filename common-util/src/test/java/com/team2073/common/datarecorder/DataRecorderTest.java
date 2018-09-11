package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable2;
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
        recorder.register(new BasicRecordable2());

        for (int i = 0; i < 30; i++) {
            recorder.record();
            ThreadUtil.sleep(100);
        }

        recorder.flush();
    }

}