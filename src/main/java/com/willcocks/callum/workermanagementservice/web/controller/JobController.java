package com.willcocks.callum.workermanagementservice.web.controller;

import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.impl.SubmitRequestToQueueEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/service/submit/extract")
    public ResponseEntity<?> extractPDFPageEndpoint(@RequestBody PDFProcessingJob rq) {
        SubmitRequestToQueueEvent event = new SubmitRequestToQueueEvent(this, rq);
         applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).build();
    }
}