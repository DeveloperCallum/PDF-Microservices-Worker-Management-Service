package com.willcocks.callum.workermanagementservice.rabbitmq.publisher;

import dto.response.ImageResponse;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@RabbitListener(queues = "imageReplyQueue")
@Component
public class ImageResponsePublisher {

    @RabbitHandler
    public void handle(ImageResponse msg){
        System.out.println(new Date().getTime());
        System.out.println("ENCODED ARR: " + msg.getImageEncodedArr().length);
    }
}
