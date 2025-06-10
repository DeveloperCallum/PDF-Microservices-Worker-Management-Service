package com.willcocks.callum.workermanagementservice.events;

import com.willcocks.callum.model.ImageRequest;
import org.springframework.context.ApplicationEvent;

public class SubmitImageRequestToQueueEvent extends ApplicationEvent {
    private ImageRequest imageRequest;

    public SubmitImageRequestToQueueEvent(Object source, ImageRequest imageRequest) {
        super(source);
        this.imageRequest = imageRequest;
    }

    public ImageRequest getImageRequest() {
        return imageRequest;
    }

    public void setImageRequest(ImageRequest imageRequest) {
        this.imageRequest = imageRequest;
    }
}
