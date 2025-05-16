package com.willcocks.callum.workermanagementservice.events;

import org.springframework.context.ApplicationEvent;

public abstract class PostExecutionEvent extends ApplicationEvent {
    public PostExecutionEvent(Object source) {
        super(source);
    }
}
