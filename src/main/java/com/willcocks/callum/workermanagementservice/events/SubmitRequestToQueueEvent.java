package com.willcocks.callum.workermanagementservice.events;

import com.willcocks.callum.model.PDFProcessingJob;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class SubmitRequestToQueueEvent extends ApplicationEvent {
    private final PDFProcessingJob job;

    public SubmitRequestToQueueEvent(Object source, PDFProcessingJob job) {
        super(source);
        this.job = job;
    }

    public PDFProcessingJob getJob() {
        return job;
    }
}
