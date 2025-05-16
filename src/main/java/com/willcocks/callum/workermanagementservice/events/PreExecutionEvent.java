package com.willcocks.callum.workermanagementservice.events;

import org.springframework.context.ApplicationEvent;

public abstract class PreExecutionEvent extends ApplicationEvent {
    public PreExecutionEvent(Object source) {
        super(source);
    }
}
