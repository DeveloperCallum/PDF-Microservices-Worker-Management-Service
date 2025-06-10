package com.willcocks.callum.workermanagementservice.webcontroller;

import com.willcocks.callum.eukrea.ServiceResolver;
import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.SubmitImageRequestToQueueEvent;
import com.willcocks.callum.workermanagementservice.events.SubmitRequestToQueueEvent;
import com.willcocks.callum.model.ImageRequest;
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
    private final String getDocumentUUID = "/api/pdf/";
    public static final Logger logger = LoggerFactory.getLogger(PdfJobController.class);

    public PdfJobController(ApplicationEventPublisher applicationEventPublisher, DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extractPDFPageEndpoint(@RequestBody PDFProcessingJob rq) {
        SubmitRequestToQueueEvent event = new SubmitRequestToQueueEvent(this, rq);

        if (rq.getDocumentUUID() != null){
            if (rq.getBase64Document() == null){ //No Document provided, so lets try and find the document using the UUID.
                String documentString = getDocumentStringFromDocumentUUID(rq.getDocumentUUID());
                rq.setBase64Document(documentString);
            }
        }

        System.out.println(rq.getCallbackService());
        System.out.println(rq.getCallbackURL());
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).body(new ReturnedResponse(rq.getDocumentUUID(), rq.getSelectionUUID(), rq.getCallbackURL(), rq.getCallbackService()));
    }

    @PostMapping("/image")
    public ResponseEntity<?> convertDocumentToImage(@RequestBody ImageRequest rq){

        if (rq.getDocumentUUID() != null){
            if (rq.getBase64Document() == null){ //No Document provided, so lets try and find the document using the UUID.
                String documentString = getDocumentStringFromDocumentUUID(rq.getDocumentUUID());
                rq.setBase64Document(documentString);
            }
        }

        applicationEventPublisher.publishEvent(new SubmitImageRequestToQueueEvent(this, rq));

        return ResponseEntity.accepted().body("");
    }

    public String getDocumentStringFromDocumentUUID(UUID documentUUID){
        ServiceInstance serviceInstance = ServiceResolver.resolveName(discoveryClient, "EXPRESSJS");
        String getDocumentUUIDWithUUID = getDocumentUUID + documentUUID.toString();

        RestClient customClient = RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory()).baseUrl(serviceInstance.getUri()).build();
        RetriedDocument response = customClient.method(HttpMethod.GET).uri(getDocumentUUIDWithUUID).retrieve().body(RetriedDocument.class);

        return response.pdfBase64();
    }

    private record ReturnedResponse(UUID documentUUID, UUID selectionUUID, String callbackURL, String callbackService){};
    private record RetriedDocument(UUID documentUUID, String pdfBase64){};

}