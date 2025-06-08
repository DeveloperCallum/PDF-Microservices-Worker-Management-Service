package com.willcocks.callum.workermanagementservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willcocks.callum.workermanagementservice.events.SubmitImageRequestToQueueEvent;
import dto.DocumentQueueEntity;
import dto.ImageRequest;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class SubmitImageRequestToQueueHandler {
    private final RabbitTemplate rabbitTemplate;

    public SubmitImageRequestToQueueHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //TODO: Figure out how to parallelize.
    @EventListener
    public void handle(SubmitImageRequestToQueueEvent event) throws JsonProcessingException {
        UUID documentUUID = event.getImageRequest().getDocumentUUID();
        String document = event.getImageRequest().getBase64Document();

        if (documentUUID == null){
            throw new IllegalStateException("DocumentUUID needs to be provided.");
        }

        if (document == null || document.isEmpty()){
            throw new IllegalStateException("Base64 string needs to be provided.");
        }

        System.out.println(new Date().getTime());

        MessageProperties properties = new MessageProperties();
        properties.setReplyTo("imageReplyQueue"); // Explicitly set reply queue
        properties.setHeader("__TypeId__", ImageRequest.class); // Tell RabbitMQ the type
        properties.setCorrelationId(UUID.randomUUID().toString());
        Message requestMessage = new Message(new ObjectMapper().writeValueAsBytes(event.getImageRequest()), properties);
        rabbitTemplate.send("myExchange", "pdf.workers.image.processing", requestMessage);
    }
}
