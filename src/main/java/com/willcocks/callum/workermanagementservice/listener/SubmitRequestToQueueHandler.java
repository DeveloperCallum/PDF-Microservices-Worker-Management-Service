package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.model.data.Selection;
import com.willcocks.callum.workermanagementservice.events.impl.PushToQueueEvent;
import com.willcocks.callum.workermanagementservice.events.impl.SubmitRequestToQueueEvent;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.ResponseService;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.DocumentResponseManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.WebhookCallback;
import com.willcocks.callum.workermanagementservice.util.Configuration;
import dto.DocumentQueueEntity;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SubmitRequestToQueueHandler {
    private final ApplicationEventPublisher applicationEventPublisher;
    private ResponseService responseService;
    private DiscoveryClient discoveryClient;
    public SubmitRequestToQueueHandler(ApplicationEventPublisher applicationEventPublisher, ResponseService responseService, DiscoveryClient discoveryClient) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.responseService = responseService;
        this.discoveryClient = discoveryClient;
    }

    @EventListener
    public void SubmitRequestToQueueEvent(SubmitRequestToQueueEvent event) {
        UUID documentUUID = event.getJob().getDocumentUUID();
        PDFProcessingJob rq = event.getJob();

        if (rq.getSelectionUUID() == null){
            throw new IllegalStateException("Missing selectionUUID");
        }

        //How many selections do we have? How many Job requests will that need?
        int totalNoRequests = (int) Math.ceil((double) rq.getSelection().size() / (double) Configuration.PAGES_PER_JOB);

        DocumentResponseManager responsesManager = new DocumentResponseManager(applicationEventPublisher, new WebhookCallback<>(rq.getCallbackService(), rq.getCallbackURL(), discoveryClient));
        responsesManager.setMinimumResponses(totalNoRequests);
        responsesManager.setDocumentUUID(rq.getDocumentUUID());
        responseService.putResponseManager(documentUUID, responsesManager); //TODO: Change to EXTRACTION UUID

        for (int requestNo = 0; requestNo < totalNoRequests; requestNo++) { //What request is this?
            int end = (requestNo * Configuration.PAGES_PER_JOB) + Configuration.PAGES_PER_JOB;
            int start = (requestNo * Configuration.PAGES_PER_JOB);

            Map<Integer, List<Selection>> totalSelections = rq.getSelection();
            Map<Integer, List<Selection>> threadSelection = new HashMap<>();

            int indexNo = 0;
            for (Map.Entry<Integer, List<Selection>> index : totalSelections.entrySet()) {
                if (indexNo >= start && indexNo < end) {
                    threadSelection.put(index.getKey(), index.getValue());
                }

                indexNo++;
            }

            System.out.printf("Submit: Job-No: %d, Start: %d, End: %d, Size: %d, Selection-Data: %s\n", requestNo, start, end, threadSelection.size(), threadSelection.toString());
            sendRequestToQueue(documentUUID, rq.getPdfBase64Document(), responsesManager, threadSelection);
        }
    }

    private void sendRequestToQueue(UUID documentUUID, String getPdfBase64Document, ResponsesManager<?> responsesManager, Map<Integer, List<Selection>> selections) {
        UUID jobUUID = UUID.randomUUID();
        DocumentQueueEntity entity = new DocumentQueueEntity(jobUUID, documentUUID, getPdfBase64Document,selections);
        entity.getDocument().setSelection(selections);
        entity.getDocument().setDocumentUUID(documentUUID);
        entity.getDocument().setPdfBase64Document(getPdfBase64Document);

        responsesManager.addExpectedResponse(documentUUID, jobUUID);

        PushToQueueEvent e = new PushToQueueEvent(this, entity);
        applicationEventPublisher.publishEvent(e);
    }
}
