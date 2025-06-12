package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.model.data.Selection;
import com.willcocks.callum.model.data.Word;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.QueueResponse;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.DocumentResponseManager;
import com.willcocks.callum.workermanagementservice.rabbitmq.service.manager.ResponsesManager;
import com.willcocks.callum.workermanagementservice.events.ExtractionCompletedEvent;
import dto.Document;
import network.ExtractionResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OnSendCallbackForExtraction {
    private static final Logger logger = LoggerFactory.getLogger(OnSendCallbackForExtraction.class);

    public OnSendCallbackForExtraction() {

    }

    @EventListener
    public void handle(ExtractionCompletedEvent event) {
        logger.info("JOB COMPLETED WITH " + event.getResponsesManager().getResponses().size() + " RESPONSES");

        //TODO: REMOVE GENERICS FROM EVENT STUFF!
        if (event.getResponsesManager() instanceof ResponsesManager) { //Use the non-generic response manager!
            DocumentResponseManager responses = (DocumentResponseManager) event.getResponsesManager();
            Document document = new Document();
            document.setDocumentUUID(responses.getDocumentUUID());

            for (QueueResponse<UUID, UUID, ExtractionResponseEntity> uuidQueueResponseEntry : responses.getResponses().values()) { //For every response
                ExtractionResponseEntity data = uuidQueueResponseEntry.getData();
                document.addSelection(data.getPayload().getDocument().getSelectionMap()); //Add the responses selection to the document object.
            }

            //Get the words from the selections list.
            Map<Integer, List<Map<Integer, List<Word>>>> words = document.getSelectionMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            integerListEntry -> integerListEntry.getValue().stream().map(Selection::getLines).collect(Collectors.toList()))
                    );

            //With the words, call the callback.
            if (responses.getCallable() != null){
                responses.getCallable().accept(words);
            }
        }
    }
}