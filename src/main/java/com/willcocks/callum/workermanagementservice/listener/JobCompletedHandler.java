package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.model.data.Selection;
import com.willcocks.callum.model.data.Word;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.DocumentResponseManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.events.JobCompletedEvent;
import dto.Document;
import dto.response.SelectionResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JobCompletedHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobCompletedHandler.class);

    public JobCompletedHandler() {

    }

    @EventListener
    public void handle(JobCompletedEvent event) {
        logger.info("JOB COMPLETED WITH " + event.getResponsesManager().getResponses().size() + " RESPONSES");


        //TODO: REMOVE GENERICS FROM EVENT STUFF!
        if (event.getResponsesManager() instanceof ResponsesManager) {
            DocumentResponseManager responses = (DocumentResponseManager) event.getResponsesManager();
            Document document = new Document();
            document.setDocumentUUID(responses.getDocumentUUID());

            for (QueueResponse<UUID, UUID, SelectionResponseEntity> uuidQueueResponseEntry : responses.getResponses().values()) {
                UUID documentUUID = uuidQueueResponseEntry.getDocumentKey();
                UUID jobUUID = uuidQueueResponseEntry.getJobKey();
                SelectionResponseEntity data = uuidQueueResponseEntry.getData();
                document.addSelection(data.getPayload().getDocument().getSelectionMap());
            }


            System.out.println(document.getSelectionMap().values().stream().map(selections -> selections.stream().map(Selection::getLines)).collect(Collectors.toList()));

            Map<Integer, List<Map<Integer, List<Word>>>> words = document.getSelectionMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            integerListEntry -> integerListEntry.getValue().stream().map(Selection::getLines).collect(Collectors.toList()))
                    );

            if (responses.getCallable() != null){
                responses.getCallable().accept(words);
            }
        }
    }
}