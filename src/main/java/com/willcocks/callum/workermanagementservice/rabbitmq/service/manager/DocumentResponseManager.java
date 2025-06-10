package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import com.willcocks.callum.model.data.Word;
import dto.extraction.SelectionResponseEntity;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DocumentResponseManager extends ResponsesManager<SelectionResponseEntity>{
    private Consumer<Map<Integer, List<Map<Integer, List<Word>>>>> callable;
    private UUID documentUUID;

    public DocumentResponseManager(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public DocumentResponseManager(ApplicationEventPublisher eventPublisher, Consumer<Map<Integer, List<Map<Integer, List<Word>>>>> callable) {
        super(eventPublisher);
        this.callable = callable;
    }

    public UUID getDocumentUUID() {
        return documentUUID;
    }

    public void setDocumentUUID(UUID documentUUID) {
        this.documentUUID = documentUUID;
    }

    public Consumer<Map<Integer, List<Map<Integer, List<Word>>>>> getCallable() {
        return callable;
    }
}
