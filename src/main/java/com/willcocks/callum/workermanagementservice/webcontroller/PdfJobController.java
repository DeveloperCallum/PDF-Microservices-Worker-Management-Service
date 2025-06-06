package com.willcocks.callum.workermanagementservice.webcontroller;

import com.willcocks.callum.eukrea.ServiceResolver;
import com.willcocks.callum.model.PDFProcessingJob;
import com.willcocks.callum.workermanagementservice.events.impl.SubmitRequestToQueueEvent;
import dto.ImageRequest;
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
            if (rq.getPdfBase64Document() == null){ //No Document provided, so lets try and find the document using the UUID.
                ServiceInstance serviceInstance = ServiceResolver.resolveName(discoveryClient, "EXPRESSJS");
                String getDocumentUUIDWithUUID = getDocumentUUID + rq.getDocumentUUID().toString();
                logger.info("getDocumentURL: " + getDocumentUUIDWithUUID);

                RestClient customClient = RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory()).baseUrl(serviceInstance.getUri()).build();
                RetriedDocument response = customClient.method(HttpMethod.GET).uri(getDocumentUUIDWithUUID).retrieve().body(RetriedDocument.class);

                logger.info(response.documentUUID().toString());
                logger.info("pdfBase64: " + response.pdfBase64().substring(0, 20));

                rq.setPdfBase64Document(response.pdfBase64());
            }
        }

        System.out.println(rq.getCallbackService());
        System.out.println(rq.getCallbackURL());
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.status(202).body(new ReturnedResponse(rq.getDocumentUUID(), rq.getSelectionUUID(), rq.getCallbackURL(), rq.getCallbackService()));
    }

    @PostMapping("/image")
    public ResponseEntity<?> convertDocumentToImage(ImageRequest rq){


        return ResponseEntity.accepted().body("");
    }

    private record ReturnedResponse(UUID documentUUID, UUID selectionUUID, String callbackURL, String callbackService){};
    private record RetriedDocument(UUID documentUUID, String pdfBase64){};

}