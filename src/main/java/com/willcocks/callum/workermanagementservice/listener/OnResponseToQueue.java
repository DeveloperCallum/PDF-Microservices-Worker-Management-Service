package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.workermanagementservice.events.ResponseFromQueueEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.ResponseService;
import network.ExtractionResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OnResponseToQueue {
    private static final Logger logger = LoggerFactory.getLogger(OnResponseToQueue.class);
    private final ResponseService responseService;

    public OnResponseToQueue(ResponseService responseService) {
        this.responseService = responseService;
    }

    @EventListener
    public void responseToQueueEventHandler(ResponseFromQueueEvent<ExtractionResponseEntity> event){
        responseService.setResponse(event.getData().getDocumentKey(), event.getData().getJobKey(), event.getData());
    }
}
