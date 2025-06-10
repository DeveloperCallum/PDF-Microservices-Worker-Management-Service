package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.workermanagementservice.events.ResponseFromQueueEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.ResponseService;
import dto.extraction.SelectionResponseEntity;
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
    public void responseToQueueEventHandler(ResponseFromQueueEvent<SelectionResponseEntity> e){
        responseService.setResponse(e.getData().getDocumentKey(), e.getData().getJobKey(), e.getData());
    }
}
