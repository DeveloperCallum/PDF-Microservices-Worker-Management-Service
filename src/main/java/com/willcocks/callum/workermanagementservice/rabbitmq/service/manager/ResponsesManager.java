package com.willcocks.callum.workermanagementservice.rabbitmq.service.manager;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.ResponseObtainedEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.ResponseWaitingEvent;
import jakarta.annotation.Nonnull;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class keeps track of the responses we receive from rabbitMQ. We need to be able to combine responses together
 * once they are all completed.
 *
 * @param <V>
 */

//TODO: Change this, this did not need to be so complicated.
public class ResponsesManager<V> implements JobResponseManager<V> {
    private int minimumResponses = -1; //1-based index.
    private final Map<UUID, QueueResponse<UUID, UUID, V>> map = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    private boolean completed = false;

    public ResponsesManager(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Add a key to the list of responses.
     * @param pageKey
     * @param jobKey
     */
    public void addExpectedResponse(@Nonnull UUID pageKey, UUID jobKey) {
        QueueResponse<UUID, UUID, V> ResponseData = new QueueResponse<UUID, UUID, V>(pageKey, jobKey);
        map.put(jobKey, ResponseData);
        eventPublisher.publishEvent(new ResponseWaitingEvent<UUID, UUID, V>(this, ResponseData));
    }

    /**
     * Add a key to the list of expected responses.
     */
    public void addExpectedResponse(@Nonnull UUID pageKey, @Nonnull UUID jobKey, V value) {
        QueueResponse<UUID, UUID, V> queueResponse = new QueueResponse<UUID, UUID, V>(pageKey, jobKey, value);
        map.put(jobKey, queueResponse);
        eventPublisher.publishEvent(new ResponseWaitingEvent<UUID, UUID, V>(this, queueResponse));
    }

    public void setResponse(@Nonnull UUID jobKey, V value){
        QueueResponse<UUID, UUID, V> queueResponse = map.get(jobKey);
        queueResponse.setData(value);

        eventPublisher.publishEvent(new ResponseObtainedEvent<>(this, queueResponse));
    }

    /**
     * Sets the minimum expected number of responses, this will stop the object from being marked as completed until the minimum is reached.
     *
     * @param minimumValue A value representing the number of responses we expect before the ResponsesObject can be marked as complete.
     */
    public void setMinimumResponses(int minimumValue) {
        minimumResponses = minimumValue;
    }

    /**
     * Check if all the responses have been received.
     *
     * @return true if completed
     */
    public boolean checkIfCompleted() {
        //We must have the full list of responses.
        if (minimumResponses > map.size()) {
            return false;
        }

        for (QueueResponse<UUID, UUID, V> data : map.values()) {
            if (!data.hasData()) {
                return false;
            }
        }

        return true;
    }

    /**
     * If completed is false, check to see if the status of responses have changed, unless, the status is already set to complete.
     *
     * @return true if completed.
     */
    public boolean isCompleted() {
        if (completed) {
            return true;
        }
        completed = checkIfCompleted();
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public Map<UUID, QueueResponse<UUID, UUID, V>> getResponses() {
        return map; //Modification is fine.
    }
}
