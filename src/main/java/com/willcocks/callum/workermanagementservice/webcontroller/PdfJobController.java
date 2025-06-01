package com.willcocks.callum.workermanagementservice.webcontroller;

import com.willcocks.callum.eukrea.ServiceResolver;
import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.impl.SubmitRequestToQueueEvent;
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
    private ApplicationEventPublisher applicationEventPublisher;
    private final DiscoveryClient discoveryClient;
    private final String getDocumentUUID = "/api/pdf/";

    public PdfJobController(ApplicationEventPublisher applicationEventPublisher, DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractPDFPageEndpoint(@RequestBody PDFProcessingJob rq) {
        SubmitRequestToQueueEvent event = new SubmitRequestToQueueEvent(this, rq);

        if (rq.getDocumentUUID() != null){
            if (rq.getPdfBase64Document() == null){ //No Document provided, so lets try and find the document using the UUID.
                ServiceInstance serviceInstance = ServiceResolver.resolveName(discoveryClient, "EXPRESSJS");
                String getDocumentUUIDWithUUID = getDocumentUUID + rq.getDocumentUUID().toString();
                System.out.println("getDocumentURL: " + getDocumentUUIDWithUUID);

                RestClient customClient = RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory()).baseUrl(serviceInstance.getUri()).build();
                retriedDocument response = customClient.method(HttpMethod.GET).uri(getDocumentUUIDWithUUID).retrieve().body(retriedDocument.class);

                System.out.println(response.documentUUID);
                System.out.println("pdfBase64: " + response.pdfBase64.substring(0, 20));

                rq.setPdfBase64Document(response.pdfBase64);
            }
        }

        System.out.println(rq.getCallbackService());
        System.out.println(rq.getCallbackURL());
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).body(new returnedResponse(rq.getDocumentUUID(), rq.getSelectionUUID(), rq.getCallbackURL(), rq.getCallbackService()));
    }

    private record returnedResponse(UUID documentUUID, UUID selectionUUID, String callbackURL, String callbackService){};
    private record retriedDocument(UUID documentUUID, String pdfBase64){};
}