package com.willcocks.callum.workermanagementservice.rabbitmq.publisher;

import com.willcocks.callum.workermanagementservice.events.ResponseFromQueueEvent;
import network.ExtractionResponseEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "documentProcessingReplyQueue")
@Component
public class DocumentResponsePublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public DocumentResponsePublisher(@Autowired ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitHandler
    public void handle(ExtractionResponseEntity message) {
        applicationEventPublisher.publishEvent(new ResponseFromQueueEvent<>(this, message));
    }
}
