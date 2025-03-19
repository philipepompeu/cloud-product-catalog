package com.github.philipepompeu.cloud_product_catalog.infra.messaging;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philipepompeu.cloud_product_catalog.application.dto.CatalogEventDto;
import com.github.philipepompeu.cloud_product_catalog.domain.service.CatalogProcessor;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class SqsConsumer {

    private final int MAX_NUMBER_OF_MESSAGES = 5;
    private final int WAIT_TIME_SECONDS = 10;

    private final String queueName;
    private final SqsClient sqsClient;
    private final CatalogProcessor catalogProcessor;
    private String queueUrl; // A URL da fila será obtida na inicialização
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    SqsConsumer(@Value("${cloud.aws.sqs.queue-name}") String queueName,
                SqsClient sqsClient,
                CatalogProcessor catalogProcessor) throws SqsException, AwsServiceException, SdkClientException, Exception{
        this.queueName = queueName;
        this.sqsClient = sqsClient;
        this.queueUrl = this.fetchQueueUrl();
        this.catalogProcessor = catalogProcessor;
    }

    private String fetchQueueUrl() throws SqsException, AwsServiceException, SdkClientException, Exception {
        return sqsClient.listQueues()
                        .queueUrls()
                        .stream()
                        .filter(url -> url.contains(this.queueName))
                        .findFirst().orElseThrow(() -> new Exception("Invalid queue name."));        
        
    }

    @Scheduled(fixedDelay = 9000)
    public void receiveMessages(){

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
                .waitTimeSeconds(WAIT_TIME_SECONDS)  // Long polling
                .build();        

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        
        Map<String, List<CatalogEventDto>> messagesGroupByOwner = messages.stream().map(message -> {
                                                                                        try {
                                                                                            return this.objectMapper.readValue(message.body(), CatalogEventDto.class);
                                                                                        } catch (Exception e) {
                                                                                            return null;                                                
                                                                                        }
                                                                                    }).filter(event -> event != null) // Remove os null antes de agrupar
                                                                                    .collect(Collectors.groupingBy(CatalogEventDto::getOwnerId));
                                                    
        messagesGroupByOwner.forEach((ownerId, messagesOfTheOwner) -> {
            System.out.println("Processando catálogo para ownerId: " + ownerId);
            
            // Chamar o método que processa o catálogo para esse ownerId
            this.processCatalog(ownerId);            
        });
        
        for (Message message : (Iterable<Message>) messages.stream()::iterator) {
            System.out.println("Mensagem sendo deletada -> " + message.body());
            this.deleteMessage(message);
        }
        
    }
    
    private void deleteMessage(Message message){
        // Deletar a mensagem após processamento
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());

    }

    private void processCatalog(String ownerId){
        catalogProcessor.process(ownerId);
    }

   

}
