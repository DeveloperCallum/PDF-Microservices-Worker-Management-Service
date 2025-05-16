package com.willcocks.callum.workermanagementservice.rabbitmq.service;

import jakarta.annotation.Nonnull;

/**
 * Keeps record of the responses and their associated IDs.
 *
 * @param <K>
 * @param <V>
 */
public class QueueResponse<K,K1,V> {
    private final K documentKey;
    private final K1 jobKey;
    private  V data = null;

    public QueueResponse(@Nonnull K documentKey, @Nonnull K1 jobKey, V data) {
        this.documentKey = documentKey;
        this.jobKey = jobKey;
        this.data = data;
    }

    public QueueResponse(K documentKey, K1 jobKey) {
        this.documentKey = documentKey;
        this.jobKey = jobKey;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public K getDocumentKey() {
        return documentKey;
    }

    public K1 getJobKey() {
        return jobKey;
    }

    public boolean hasData() {
        return data != null;
    }
}
