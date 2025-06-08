package com.willcocks.callum.workermanagementservice.events;

import dto.DocumentQueueEntity;
import org.springframework.context.ApplicationEvent;

public class PushToQueueEvent extends ApplicationEvent {
    private final DocumentQueueEntity entity;

    public PushToQueueEvent(Object source, DocumentQueueEntity entity) {
        super(source);
        this.entity = entity;
    }

    public DocumentQueueEntity getQueueEntity() {
        return entity;
    }
}

