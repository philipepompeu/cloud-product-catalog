package com.github.philipepompeu.cloud_product_catalog.infra.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import com.github.philipepompeu.cloud_product_catalog.domain.service.CatalogProcessor;

@SpringBootTest
@DisplayName("Unit test of the SQS Consumer")
public class SqsConsumerTest {

    @Value("${cloud.aws.sqs.queue-name}")
    private String queueName;

    @Mock
    private SqsClient sqsClient; // Mock do SQS

    @Mock
    private CatalogProcessor catalogProcessor;    
    
    @Test
    @DisplayName("should process the messages in the SQS queue.")
    void shouldProcessMessagesFromSQSQueue() {
        // Mocka a listagem de filas
        String queueUrl = String.format("https://sqs.us-east-1.amazonaws.com/123456789012/%s", queueName);
        
        ListQueuesResponse listQueuesResponse = ListQueuesResponse.builder()
            .queueUrls(queueUrl)
            .build();
        
        when(sqsClient.listQueues()).thenReturn(listQueuesResponse);
        
        // Mocka o recebimento de mensagens
        Message message = Message.builder()
            .body("{\"ownerId\":\"12345\"}")
            .receiptHandle("msg-receipt-handle")
            .build();
        
        ReceiveMessageResponse receiveMessageResponse = ReceiveMessageResponse.builder()
            .messages(List.of(message))
            .build();
        
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(receiveMessageResponse);

        // Mocka a exclusão da mensagem
        when(sqsClient.deleteMessage(any(DeleteMessageRequest.class))).thenReturn(DeleteMessageResponse.builder().build());

        SqsConsumer sqsConsumer = new SqsConsumer(queueName, sqsClient, catalogProcessor);
        
        sqsConsumer.receiveMessages();

        // Verifica se os métodos do SQS foram chamados corretamente
        verify(sqsClient, times(1)).listQueues();
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        verify(catalogProcessor, times(1)).process("12345");
    }
    
}
