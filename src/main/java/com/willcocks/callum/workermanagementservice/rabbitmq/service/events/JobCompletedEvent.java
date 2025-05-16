package com.willcocks.callum.workermanagementservice.rabbitmq.service.events;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.JobResponseManager;

public class JobCompletedEvent {
    private final JobResponseManager responsesManager;

    public JobCompletedEvent(JobResponseManager responses) {
        this.responsesManager = responses;
    }

    public JobResponseManager getResponsesManager() {
        return responsesManager;
    }
}
