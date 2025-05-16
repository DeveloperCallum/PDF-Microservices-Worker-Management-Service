package com.willcocks.callum.workermanagementservice.rabbitmq.listener;

import com.willcocks.callum.workermanagementservice.events.impl.ResponseFromQueueEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.ResponseService;
import dto.DocumentQueueEntity;
import dto.response.DocumentResponseEntity;
import dto.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResponseToQueueHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseToQueueHandler.class);
    private final ResponseService responseService;

    public ResponseToQueueHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @EventListener
    public void responseToQueueEventHandler(ResponseFromQueueEvent<DocumentResponseEntity> e){
        responseService.setResponse(e.getData().getPageKey(), e.getData().getJobKey(), e.getData());
    }
}
