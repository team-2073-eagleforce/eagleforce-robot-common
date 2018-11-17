package com.team2073.common.datarecorder.model;

/**
 * @author Preston Briggs
 */
public class DelegatingRecordableWrapper extends RecordableWrapper {

    private final LifecycleAwareRecordable recordable;

    public DelegatingRecordableWrapper(LifecycleAwareRecordable recordable) {
        this.recordable = recordable;
    }

    @Override
    public Object getInstance() {
        return recordable;
    }

    @Override
    public void onBeforeRecord() {
        recordable.onBeforeRecord();
    }

    @Override
    public void onAfterRecord() {
        recordable.onAfterRecord();
    }

}
