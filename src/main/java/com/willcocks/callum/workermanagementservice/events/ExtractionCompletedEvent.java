package com.willcocks.callum.workermanagementservice.events;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.JobResponseManager;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class ExtractionCompletedEvent extends ApplicationEvent {
    private final JobResponseManager responsesManager;

    public ExtractionCompletedEvent(Object source, JobResponseManager responsesManager) {
        super(source);
        this.responsesManager = responsesManager;
    }

    public JobResponseManager getResponsesManager() {
        return responsesManager;
    }
}
