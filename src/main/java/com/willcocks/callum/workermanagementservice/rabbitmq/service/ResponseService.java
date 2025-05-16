package com.willcocks.callum.workermanagementservice.rabbitmq.service;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.JobCompletedEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.ResponseObtainedEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResponseService {
    private final Map<UUID, ResponsesManager<?>> arrMap = new HashMap<>();
    public ResponsesManager<?> getResponseManager(UUID pageKey){
        return arrMap.get(pageKey);
    }
    private final ApplicationEventPublisher eventPublisher;

    public ResponseService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void putResponseManager(UUID pageKey, ResponsesManager<?> value){
        arrMap.put(pageKey, value);
    }

    public <V> void setResponse(UUID pageKey, UUID jobKey, V value){
        ResponsesManager<V> manager = (ResponsesManager<V>) getResponseManager(pageKey);
        manager.setResponse(jobKey, value);
    }

    @EventListener
    public void listenToEvent(ResponseObtainedEvent<UUID, UUID, ?> event){
        ResponsesManager<?> responsesManager = arrMap.get(event.getQueueResponse().getDocumentKey());
        boolean isCompleted = responsesManager.checkIfCompleted();

        if (!isCompleted){
            return;
        }

        JobCompletedEvent jobCompletedEvent = new JobCompletedEvent(responsesManager);
        eventPublisher.publishEvent(jobCompletedEvent);
    }
}