package com.willcocks.callum.workermanagementservice.events;

public abstract class PreExecutionPayloadEvent<V> extends PreExecutionEvent{
    public PreExecutionPayloadEvent(Object source) {
        super(source);
    }

    public V payload;

    public V getPayload() {
        return payload;
    }

    public void setPayload(V payload) {
        this.payload = payload;
    }
}
