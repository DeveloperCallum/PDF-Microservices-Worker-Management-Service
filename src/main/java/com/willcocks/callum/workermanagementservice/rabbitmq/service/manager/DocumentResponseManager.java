package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import dto.Document;
import dto.response.SelectionResponseEntity;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;
import java.util.function.Consumer;

public class DocumentResponseManager extends ResponsesManager<SelectionResponseEntity>{
    private Consumer<Document> callable;
    private UUID documentUUID;

    public DocumentResponseManager(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public DocumentResponseManager(ApplicationEventPublisher eventPublisher, Consumer<Document> callable) {
        super(eventPublisher);
        this.callable = callable;
    }

    public UUID getDocumentUUID() {
        return documentUUID;
    }

    public void setDocumentUUID(UUID documentUUID) {
        this.documentUUID = documentUUID;
    }

    public Consumer<Document> getCallable() {
        return callable;
    }
}
