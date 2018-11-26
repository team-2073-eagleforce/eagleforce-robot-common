package com.team2073.common.datarecorder.model;

import com.team2073.common.assertion.Assert;

/**
 * Wraps any Object so it can be a recordable. Only meant to be used internally.
 *
 * @author Preston Briggs
 */
public class ObjectWrapper extends RecordableWrapper {

    private final Object instance;

    public ObjectWrapper(Object instance) {
        Assert.assertNotNull(instance, "instance");
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public void onBeforeRecord() {
        // no op
    }

    @Override
    public void onAfterRecord() {
        // no op
    }
}
