package com.github.philipepompeu.cloud_product_catalog.infra.messaging;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    
    public SqsConsumer(@Value("${cloud.aws.sqs.queue-name}") String queueName,
                SqsClient sqsClient,
                CatalogProcessor catalogProcessor){
        this.queueName = queueName;
        this.sqsClient = sqsClient;
        this.catalogProcessor = catalogProcessor;

        try {
            this.queueUrl = this.fetchQueueUrl();
        } catch (Exception e) {
            System.out.println(String.format("Fail to initialize SqsConsumer [ %s ]", e.getMessage() ));
        }
        
    }

    private String fetchQueueUrl() throws SqsException, AwsServiceException, SdkClientException, Exception {
        return sqsClient.listQueues()
                        .queueUrls()
                        .stream()
                        .filter(url -> url.contains(this.queueName))
                        .findFirst().orElseThrow(() -> new Exception("Invalid queue name."));        
        
    }

    @Scheduled(fixedDelayString = "${scheduler.queue.consumer.delay}")
    public void receiveMessages(){


        if (queueUrl instanceof String) {
            
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
                    .waitTimeSeconds(WAIT_TIME_SECONDS)  // Long polling
                    .build();        

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            if (messages.size() > 0) {
                Function<Message, CatalogEventDto> dtoConverter = message -> {
                    try {
                        return this.objectMapper.readValue(message.body(), CatalogEventDto.class);
                    } catch (Exception e) {
                        return null;
                    }
                };
                
                Map<String, List<CatalogEventDto>> messagesGroupByOwner = messages.stream().map(dtoConverter)
                                                                                            .filter(event -> event != null && event.getOwnerId() != null) // Remove os null antes de agrupar
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


    public String getQueueUrl(){ return this.queueUrl; }


   

}
