package com.willcocks.callum.workermanagementservice.events;

import dto.DocumentMetaQueueEntity;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class OnSendDocumentRequestToQueue extends ApplicationEvent {
    private final DocumentMetaQueueEntity documentMetaQueueEntity;

    public OnSendDocumentRequestToQueue(Object source, DocumentMetaQueueEntity documentMetaQueueEntity) {
        super(source);
        this.documentMetaQueueEntity = documentMetaQueueEntity;
    }

    public DocumentMetaQueueEntity getDocumentMetaQueueEntity() {
        return documentMetaQueueEntity;
    }
}
