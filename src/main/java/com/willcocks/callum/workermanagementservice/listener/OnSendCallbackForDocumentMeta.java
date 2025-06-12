package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.model.SendWebhookToService;
import com.willcocks.callum.workermanagementservice.events.DocumentMetaCompletedEvent;
import network.DocumentMetaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendCallbackForDocumentMeta {
    private final DiscoveryClient discoveryClient;
    private final Logger logger = LoggerFactory.getLogger(OnSendCallbackForDocumentMeta.class);
    public OnSendCallbackForDocumentMeta(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @EventListener
    public void handle(DocumentMetaCompletedEvent event) {
        String callbackURL = event.getResponse().getCallbackURL();
        String serviceName = event.getResponse().getCallbackService();

        if (callbackURL == null || callbackURL.isEmpty()){
            throw new IllegalStateException("CallbackURL not provided!");
        }

        if (serviceName == null || serviceName.isEmpty()){
            throw new IllegalStateException("CallbackURL not provided!");
        }

        try{
            //Create & send a new webhook
            SendWebhookToService<DocumentMetaResponse> hook = new SendWebhookToService<>(serviceName, callbackURL, discoveryClient);
            hook.accept(event.getResponse());
        }catch (RuntimeException e){
            logger.error(e.getMessage(), e);
        }
    }
}
