package com.willcocks.callum.workermanagementservice.events;

import com.willcocks.callum.model.PDFProcessingJob;
import org.springframework.context.ApplicationEvent;

public class SubmitDocumentRequestToQueueEvent extends ApplicationEvent {
    private final PDFProcessingJob job;

    public SubmitDocumentRequestToQueueEvent(Object source, PDFProcessingJob job) {
        super(source);
        this.job = job;
    }

    public PDFProcessingJob getJob() {
        return job;
    }
}
