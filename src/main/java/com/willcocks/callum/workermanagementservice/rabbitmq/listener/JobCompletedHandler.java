package com.willcocks.callum.workermanagementservice.rabbitmq.listener;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.DocumentResponseManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.JobCompletedEvent;
import dto.response.SelectionResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JobCompletedHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobCompletedHandler.class);

    @EventListener
    public void handle(JobCompletedEvent event) {
        logger.info("JOB COMPLETED WITH " + event.getResponsesManager().getResponses().size() + " RESPONSES");

        //TODO: REMOVE GENERICS FROM EVENT STUFF!
        if (event.getResponsesManager() instanceof ResponsesManager) {
            DocumentResponseManager responses = (DocumentResponseManager) event.getResponsesManager();

            for (QueueResponse<UUID, UUID, SelectionResponseEntity> uuidQueueResponseEntry : responses.getResponses().values()) {
                UUID documentUUID = uuidQueueResponseEntry.getDocumentKey();
                UUID jobUUID = uuidQueueResponseEntry.getJobKey();
                UUID selectionUUID = uuidQueueResponseEntry.getData().getSelectionUUID();
                SelectionResponseEntity data = uuidQueueResponseEntry.getData();

                System.out.printf("Job: %s, SelectionUUID: %s, Document: %s, Data: %s \n", jobUUID, selectionUUID, documentUUID, data.getPayload()
                        .getSelectionMap().values().stream()
                        .map(selections -> selections.stream().map(selection ->  selection.getLines().toString())
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList()));
            }
        }
    }
}