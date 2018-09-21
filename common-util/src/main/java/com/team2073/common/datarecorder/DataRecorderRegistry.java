package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.model.Recordable;
import com.team2073.common.util.Throw;

/**
 * @author pbriggs
 */
public class DataRecorderRegistry {

    private static DataRecorder instance;
    private static boolean started;

    public static void register(Recordable recordable) {
        getInstance().register(recordable);
    }

    private void start() {
        if (!started)
            started = true;
    }

    public static DataRecorder getInstance() {
        if (instance == null)
            instance = new DataRecorder();

        return instance;
    }

    public static void setInstance(DataRecorder instance) {
        if (DataRecorderRegistry.instance != null) {
            Throw.illegalState("Cannot set [%s] after it has already been set. Either setInstance(...) was " +
                    "called twice or getInstance()/register() was called prior to calling setInstance(...).",
                    DataRecorder.class.getSimpleName());
        }
        DataRecorderRegistry.instance = instance;
    }
}
