package com.willcocks.callum.workermanagementservice.rabbitmq.publisher;

import com.willcocks.callum.model.ImageResponse;
import com.willcocks.callum.workermanagementservice.events.ImageCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.Date;

@RabbitListener(queues = "imageReplyQueue")
@Component
public class ImageResponsePublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public ImageResponsePublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitHandler
    public void handle(ImageResponse msg){
        System.out.println(new Date().getTime());
        System.out.println("ENCODED ARR: " + msg.getImageEncodedArr().length);

        applicationEventPublisher.publishEvent(new ImageCompletedEvent(this, msg));
    }
}