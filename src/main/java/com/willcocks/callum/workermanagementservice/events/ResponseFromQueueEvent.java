package com.willcocks.callum.workermanagementservice.events;

import org.springframework.context.ApplicationEvent;

public class ResponseFromQueueEvent<T>  extends ApplicationEvent {
    public T t;

    public ResponseFromQueueEvent(Object source, T t) {
        super(source);
        this.t = t;
    }

    public T getData() {
        return t;
    }
}
