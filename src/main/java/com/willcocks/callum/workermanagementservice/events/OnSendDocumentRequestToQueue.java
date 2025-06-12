package com.willcocks.callum.workermanagementservice.events;

import network.DocumentMetaRequest;
import org.springframework.context.ApplicationEvent;

public class OnSendDocumentRequestToQueue extends ApplicationEvent {
    private final DocumentMetaRequest documentMetaRequest;

    public OnSendDocumentRequestToQueue(Object source, DocumentMetaRequest documentMetaRequest) {
        super(source);
        this.documentMetaRequest = documentMetaRequest;
    }

    public DocumentMetaRequest getDocumentMetaQueueEntity() {
        return documentMetaRequest;
    }
}
