package com.willcocks.callum.workermanagementservice.web.controller;

import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.impl.SubmitRequestToQueueEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/management/pdf")
public class PdfJobController {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/extract")
    public ResponseEntity<?> extractPDFPageEndpoint(@RequestBody PDFProcessingJob rq) {
        SubmitRequestToQueueEvent event = new SubmitRequestToQueueEvent(this, rq);

        System.out.println(rq.getCallbackService());
        System.out.println(rq.getCallbackURL());
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).body(new returnedResponse(rq.getDocumentUUID(), rq.getSelectionUUID(), rq.getCallbackURL(), rq.getCallbackService()));
    }

    private record returnedResponse(UUID documentUUID, UUID selectionUUID, String callbackURL, String callbackService){};
}