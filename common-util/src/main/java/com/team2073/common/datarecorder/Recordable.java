package com.team2073.common.datarecorder;

/**
 * @author pbriggs
 */
public interface Recordable {

    /** Called directly before this object is recorded. Update any values so they are current. */
    void onBeforeRecord();

}
