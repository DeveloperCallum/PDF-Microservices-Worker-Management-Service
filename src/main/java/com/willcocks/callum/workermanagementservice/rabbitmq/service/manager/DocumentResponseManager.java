package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import dto.response.SelectionResponseEntity;
import org.springframework.context.ApplicationEventPublisher;

public class DocumentResponseManager extends ResponsesManager<SelectionResponseEntity>{
    public DocumentResponseManager(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }
}
