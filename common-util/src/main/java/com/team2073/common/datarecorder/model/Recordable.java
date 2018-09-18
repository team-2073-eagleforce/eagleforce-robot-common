package com.team2073.common.datarecorder.model;

/**
 * @author pbriggs
 */
public interface Recordable {

    /** Called directly before this object is recorded. Update any values so they are current. */
    default void onBeforeRecord() {}

    /** Called directly after this object is recorded. Release any synchronization locks here. */
    default void onAfterRecord() {}

}
