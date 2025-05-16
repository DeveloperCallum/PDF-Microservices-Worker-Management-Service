package com.willcocks.callum.workermanagementservice.rabbitmq.listener;

import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.DocumentResponseManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.JobCompletedEvent;
import dto.response.DocumentResponseEntity;
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

            for (QueueResponse<UUID, UUID, DocumentResponseEntity> uuidQueueResponseEntry : responses.getResponses().values()) {
                UUID pageUUID = uuidQueueResponseEntry.getDocumentKey();
                UUID jobUUID = uuidQueueResponseEntry.getJobKey();
                DocumentResponseEntity data = uuidQueueResponseEntry.getData();

                System.out.printf("Job: %s, Page: %s, Data: %s \n", jobUUID, pageUUID, data.getPayload()
                        .getSelectionMap().values().stream()
                        .map(selections -> selections.stream().map(selection ->  selection.getLines().toString())
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList()));
            }
        }
    }
}