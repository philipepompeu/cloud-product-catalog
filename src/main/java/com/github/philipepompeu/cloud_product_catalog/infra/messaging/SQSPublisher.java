package com.github.philipepompeu.cloud_product_catalog.infra.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philipepompeu.cloud_product_catalog.application.dto.CatalogEventDto;
import com.github.philipepompeu.cloud_product_catalog.domain.observer.CatalogEventListener;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class SQSPublisher implements CatalogEventListener{

    private final SqsClient sqsClient;
    private final String queueName;
    private String queueUrl; // A URL da fila será obtida na inicialização
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SQSPublisher(SqsClient sqsClient, @Value("${cloud.aws.sqs.queue-name}") String queueName){        
        this.sqsClient = sqsClient;  
        this.queueName = queueName;  
        
        try {
            this.queueUrl = this.fetchQueueUrl();            
        } catch (Exception e) {
            System.out.println(String.format("Fail to initialize SQSPublisher [ %s ]", e.getMessage() ));
        }
    }

    private String fetchQueueUrl() throws SqsException, AwsServiceException, SdkClientException, Exception {
        return sqsClient.listQueues()
                        .queueUrls()
                        .stream()
                        .filter(url -> url.contains(this.queueName))
                        .findFirst()
                        .orElseThrow(() -> new Exception("Invalid queue name."));        
        
    }

    
    @Override
    public void onCatalogUpdated(String ownerId) {  
        
        if (queueUrl instanceof String) {

            System.out.println(String.format("Catalog of ownerId[%s] updated. queueName=[%s]", ownerId, this.queueName));        
            try {
                String message = objectMapper.writeValueAsString(new CatalogEventDto(ownerId));
                
                SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(message)
                        .build();
        
                sqsClient.sendMessage(sendMsgRequest);
                
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }
        }
        

    }

}
