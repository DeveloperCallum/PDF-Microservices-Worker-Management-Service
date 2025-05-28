package com.willcocks.callum.workermanagementservice.rabbitmq.service;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClient;
import java.util.HashMap;
import java.util.function.Consumer;

public class WebhookCallback<T> implements Consumer<T> {
    private String serviceName;
    private String callbackURL;
    private final DiscoveryClient discoveryClient;

    public WebhookCallback(String serviceName, String callbackURL, DiscoveryClient discoveryClient) {
        this.serviceName = serviceName;
        this.callbackURL = callbackURL;
        this.discoveryClient = discoveryClient;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    //TODO: HTTPs?
    @Retryable(
            maxAttempts = 3, // Number of retries
            backoff = @Backoff(delay = 5000) // Delay in milliseconds (5 seconds)
    )
    @Override
    public void accept(T t) {
        ServiceInstance serviceInstance = discoveryClient.getInstances(serviceName).get(0);

        System.out.println("URL: " + serviceInstance.getUri() + "/" + callbackURL); //TODO: SEND REQUEST TO URL!

        RestClient customClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(serviceInstance.getUri())
                .build();

        ResponseEntity<Void> response = customClient.method(HttpMethod.POST)
                .uri(callbackURL)
                .body(t)
                .retrieve()
                .toBodilessEntity();

        System.out.println("Status Code: " + response.getStatusCode());

        if (response.getStatusCode() != HttpStatusCode.valueOf(202)){
            System.out.println("Issue sending callback!");
            //TODO? Event?
        }
    }
}
