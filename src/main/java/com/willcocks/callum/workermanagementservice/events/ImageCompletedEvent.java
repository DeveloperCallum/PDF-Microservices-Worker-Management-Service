package com.willcocks.callum.workermanagementservice.events;

import com.willcocks.callum.model.ImageRequest;
import com.willcocks.callum.model.ImageResponse;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class ImageCompletedEvent extends ApplicationEvent {
    private ImageResponse imageResponse;

    public ImageCompletedEvent(Object source, ImageResponse imageResponse) {
        super(source);
        this.imageResponse = imageResponse;
    }

    public ImageResponse getImageResponse() {
        return imageResponse;
    }

    public void setImageResponse(ImageResponse imageResponse) {
        this.imageResponse = imageResponse;
    }
}
