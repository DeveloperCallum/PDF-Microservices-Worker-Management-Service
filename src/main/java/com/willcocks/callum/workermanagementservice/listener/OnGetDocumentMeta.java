package com.willcocks.callum.workermanagementservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willcocks.callum.workermanagementservice.events.OnSendDocumentRequestToQueue;
import dto.DocumentMetaQueueEntity;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OnGetDocumentMeta {
    private final RabbitTemplate rabbitTemplate;

    public OnGetDocumentMeta(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @EventListener
    public void handle(OnSendDocumentRequestToQueue event) throws JsonProcessingException {
        MessageProperties properties = new MessageProperties();
        properties.setReplyTo(""); // Explicitly set reply queue
        properties.setHeader("__TypeId__", DocumentMetaQueueEntity.class.getName()); // Tell RabbitMQ the type
        properties.setHeader("X-Trace-Id", MDC.get("traceId")); //Set the traceId to be used by other requests.
        properties.setCorrelationId(UUID.randomUUID().toString());
        Message requestMessage = new Message(new ObjectMapper().writeValueAsBytes(event.getDocumentMetaQueueEntity()), properties);
        rabbitTemplate.send("myExchange", "pdf.workers.document.meta", requestMessage);
    }
}
