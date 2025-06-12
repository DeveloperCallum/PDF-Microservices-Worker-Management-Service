package com.willcocks.callum.workermanagementservice.web.controller;

import com.willcocks.callum.eukrea.ServiceResolver;
import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.OnSendDocumentRequestToQueue;
import com.willcocks.callum.workermanagementservice.events.SubmitImageRequestToQueueEvent;
import com.willcocks.callum.workermanagementservice.events.SubmitDocumentRequestToQueueEvent;
import com.willcocks.callum.model.ImageRequest;
import network.DocumentMetaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/management/pdf")
public class PdfJobController {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DiscoveryClient discoveryClient;
    public static final Logger logger = LoggerFactory.getLogger(PdfJobController.class);

    public PdfJobController(ApplicationEventPublisher applicationEventPublisher, DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractPDFPageEndpoint(@RequestBody PDFProcessingJob rq) {
        SubmitDocumentRequestToQueueEvent event = new SubmitDocumentRequestToQueueEvent(this, rq);

        if (rq.getDocumentUUID() == null) {
            throw new IllegalStateException("DocumentUUID not provided!");
        }

        if (rq.getBase64Document() == null) { //No Document provided, so lets try and find the document using the UUID.
            String documentString = getDocumentStringFromDocumentUUID(rq.getDocumentUUID());

            if (documentString == null){
                return ResponseEntity.status(404).body("");
            }

            rq.setBase64Document(documentString);
        }

        System.out.println(rq.getCallbackService());
        System.out.println(rq.getCallbackURL());
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).body(new ReturnedResponse(rq.getDocumentUUID(), rq.getSelectionUUID(), rq.getCallbackURL(), rq.getCallbackService()));
    }

    @PostMapping("/image")
    public ResponseEntity<?> convertDocumentToImage(@RequestBody ImageRequest rq) {

        if (rq.getDocumentUUID() == null) {
            throw new IllegalStateException("DocumentUUID not provided!");
        }

        if (rq.getBase64Document() == null) { //No Document provided, so lets try and find the document using the UUID.
            String documentString = getDocumentStringFromDocumentUUID(rq.getDocumentUUID());

            if (documentString == null){
                return ResponseEntity.status(404).body("");
            }

            rq.setBase64Document(documentString);
        }

        applicationEventPublisher.publishEvent(new SubmitImageRequestToQueueEvent(this, rq));
        return ResponseEntity.ok("");
    }

    @PostMapping("/meta")
    public ResponseEntity<?> getDocumentMetaData(@RequestBody DocumentMetaRequest rq) {
        String callbackURL = rq.getCallbackURL();
        String serviceName = rq.getCallbackService();

        //If we don't have a place to send the data, the data is meaningless.
        if (callbackURL == null || callbackURL.isEmpty()) {
            throw new IllegalStateException("CallbackURL not provided!");
        }

        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalStateException("ServiceName not provided!");
        }

        if (rq.getBase64Document() == null) { //No Document provided, so lets try and find the document using the UUID.
            String documentString = getDocumentStringFromDocumentUUID(rq.getDocumentUUID());

            if (documentString == null){
                return ResponseEntity.status(404).body("Document could be not found using UUID");
            }

            rq.setBase64Document(documentString);
        }

        try{
            applicationEventPublisher.publishEvent(new OnSendDocumentRequestToQueue(this, rq));
        }catch (RuntimeException e){
            logger.error(e.getMessage(), e);
        }

        System.out.println("");
        return ResponseEntity.ok().build();
    }

    /**
     * Get the base64 document string from a document UUID.
     * @param documentUUID The unique id.
     * @return the document base64 string or null if not found.
     */
    public String getDocumentStringFromDocumentUUID(UUID documentUUID) {
        ServiceInstance serviceInstance = ServiceResolver.resolveName(discoveryClient, "EXPRESSJS");
        String getDocumentUUID = "/api/pdf/";
        String getDocumentUUIDWithUUID = getDocumentUUID + documentUUID.toString();

        try{
            RestClient customClient = RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory()).baseUrl(serviceInstance.getUri()).build();
            RetriedDocument response = customClient.method(HttpMethod.GET).uri(getDocumentUUIDWithUUID).retrieve().body(RetriedDocument.class);
            return response.pdfBase64();
        }catch (RuntimeException e){
            return null;
        }
    }

    private record ReturnedResponse(UUID documentUUID, UUID selectionUUID, String callbackURL, String callbackService) {

    }

    private record RetriedDocument(UUID documentUUID, String pdfBase64) {

    }

    ;

}