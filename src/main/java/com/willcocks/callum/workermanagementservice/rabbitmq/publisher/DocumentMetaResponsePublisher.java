package com.willcocks.callum.workermanagementservice.rabbitmq.publisher;

import com.willcocks.callum.workermanagementservice.events.DocumentMetaCompletedEvent;
import dto.Image;
import network.DocumentMetaResponse;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "metaReplyQueue")
@Component
public class DocumentMetaResponsePublisher {
    private ApplicationEventPublisher applicationEventPublisher;

    public DocumentMetaResponsePublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitHandler
    public void handle(DocumentMetaResponse msg){
        applicationEventPublisher.publishEvent(new DocumentMetaCompletedEvent(this, msg));
    }
}
