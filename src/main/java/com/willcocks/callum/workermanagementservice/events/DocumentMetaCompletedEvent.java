package com.willcocks.callum.workermanagementservice.events;

import dto.Image;
import network.DocumentMetaResponse;
import org.springframework.context.ApplicationEvent;

public class DocumentMetaCompletedEvent extends ApplicationEvent {
    private DocumentMetaResponse response;

    private DocumentMetaCompletedEvent(Object source) {
        super(source);
    }

    public DocumentMetaCompletedEvent(Object source, DocumentMetaResponse response) {
        this(source);
        this.response = response;
    }

    public DocumentMetaResponse getResponse() {
        return response;
    }
}
