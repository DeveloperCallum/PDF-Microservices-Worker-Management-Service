package com.willcocks.callum.workermanagementservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willcocks.callum.workermanagementservice.events.impl.PushToQueueEvent;
import dto.DocumentQueueEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Queue;

@Component
public class PushToQueueHandler {
    private final RabbitTemplate rabbitTemplate;

    public PushToQueueHandler(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @EventListener
    public void handlePushToQueueEvent(PushToQueueEvent event) throws JsonProcessingException {
        MessageProperties properties = new MessageProperties();
        properties.setReplyTo("workerReplyQueue"); // Explicitly set reply queue
        properties.setHeader("__TypeId__", DocumentQueueEntity.class.getName()); // Tell RabbitMQ the type
        properties.setCorrelationId(event.getQueueEntity().getJobUUID().toString());
        Message requestMessage = new Message(new ObjectMapper().writeValueAsBytes(event.getQueueEntity()), properties);
        rabbitTemplate.send("myExchange", "pdf.workers.document.processing", requestMessage);
    }
}