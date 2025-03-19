package com.github.philipepompeu.cloud_product_catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(getAppRegion())
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .region(getAppRegion())
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    private Region getAppRegion(){
        return Region.of(System.getProperty("cloud.aws.region", "sa-east-1"));
    }

    private ProfileCredentialsProvider getCredentialsProvider(){
        return ProfileCredentialsProvider.create("default");// Usa as credenciais da ~/.aws
    }
    
}
