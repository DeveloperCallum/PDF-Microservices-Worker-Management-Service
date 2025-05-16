package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import dto.response.DocumentResponseEntity;
import org.springframework.context.ApplicationEventPublisher;

public class DocumentResponseManager extends ResponsesManager<DocumentResponseEntity>{
    public DocumentResponseManager(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }
}
