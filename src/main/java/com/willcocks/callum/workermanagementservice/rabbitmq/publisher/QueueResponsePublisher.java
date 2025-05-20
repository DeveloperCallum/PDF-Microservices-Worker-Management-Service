package com.willcocks.callum.workermanagementservice.rabbitmq.publisher;

import com.willcocks.callum.workermanagementservice.events.impl.ResponseFromQueueEvent;
import dto.response.SelectionResponseEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "workerReplyQueue")
@Component
public class QueueResponsePublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public QueueResponsePublisher(@Autowired ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitHandler
    public void handleRabbitMQMessage(SelectionResponseEntity message) {
        applicationEventPublisher.publishEvent(new ResponseFromQueueEvent<>(this, message));
    }
}
