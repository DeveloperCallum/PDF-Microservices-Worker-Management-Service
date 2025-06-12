package com.willcocks.callum.workermanagementservice.events;

import network.ExtractionRequest;
import org.springframework.context.ApplicationEvent;

public class PushToQueueEvent extends ApplicationEvent {
    private final ExtractionRequest entity;

    public PushToQueueEvent(Object source, ExtractionRequest entity) {
        super(source);
        this.entity = entity;
    }

    public ExtractionRequest getQueueEntity() {
        return entity;
    }
}

