package com.willcocks.callum.workermanagementservice.listener;

import com.willcocks.callum.model.ImageRequest;
import com.willcocks.callum.model.SendWebhookToService;
import com.willcocks.callum.workermanagementservice.events.ImageCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendCallbackForImage {
    private static final Logger logger = LoggerFactory.getLogger(OnSendCallbackForExtraction.class);
    private final DiscoveryClient discoveryClient;

    public OnSendCallbackForImage(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @EventListener
    public void handle(ImageCompletedEvent event){
        logger.debug("Images Received, sending webhook.");
        ImageRequest imageRequest = event.getImageResponse().getRequest(); //Get the image request
        String[] base64ImagesArr = event.getImageResponse().getImageEncodedArr();

        //We need to split the pages up, this will make them more manageable.
        int i = 0;
        String[] toSent = new String[10];
        do {
            int pos = i % 5; //For every 10 images, create a new array that contains chunks of data.
            toSent[pos] = base64ImagesArr[i];

            if (pos == 0 && i != 0){
                ImageWebhookData ImageWebhookData = new ImageWebhookData(base64ImagesArr.length, toSent);

                //Create & send a new webhook.
                SendWebhookToService<ImageWebhookData> sendWebhookToService = new SendWebhookToService<>(imageRequest.getCallbackService(),
                        imageRequest.getCallbackURL(), discoveryClient);

                try{
                    sendWebhookToService.accept(ImageWebhookData);
                    logger.error("Webhook sent..");
                }catch (RuntimeException e){
                    logger.info("Error sending webhook!", e);
                }finally {
                    toSent = new String[10];
                }
            }

            i++;
        }while (i <= base64ImagesArr.length);
    }

    private record ImageWebhookData(int numberOfImages, String[] imageChunks){};
}