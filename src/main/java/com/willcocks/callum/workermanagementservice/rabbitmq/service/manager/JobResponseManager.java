package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;

import java.util.Map;
import java.util.UUID;

public interface JobResponseManager<V> {
    Map<UUID, QueueResponse<UUID, UUID, V>> getResponses();
}
