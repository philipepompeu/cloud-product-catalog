package com.github.philipepompeu.cloud_product_catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(System.getProperty("cloud.aws.region", "sa-east-1")))
                .credentialsProvider(ProfileCredentialsProvider.create("default")) // Usa as credenciais da ~/.aws
                .build();
    }
    
}
