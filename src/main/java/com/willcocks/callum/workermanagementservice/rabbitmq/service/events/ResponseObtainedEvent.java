package com.willcocks.callum.workermanagementservice.rabbitmq.service.events;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import org.springframework.context.ApplicationEvent;

public class ResponseObtainedEvent<K,K1,V> extends ApplicationEvent {
    private final QueueResponse<K,K1,V> queueResponse;

    public ResponseObtainedEvent(Object source, QueueResponse<K, K1, V> e) {
        super(source);
        this.queueResponse = e;
    }

    public QueueResponse<K, K1, V> getQueueResponse() {
        return queueResponse;
    }
}
