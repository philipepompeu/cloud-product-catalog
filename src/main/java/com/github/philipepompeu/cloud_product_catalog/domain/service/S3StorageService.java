package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.github.philipepompeu.cloud_product_catalog.infra.storage.StorageService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService implements StorageService{

    private final S3Client s3Client;
    private final String bucketName = "protheus-lexicon";

    S3StorageService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    @Override
    public void uploadJsonCatalogToTheCloud(String ownerId, String jsonCatalog) {        
        String key = "catalogs/" + ownerId + ".json";

        System.out.println("Gerando json -> "+ key);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/json")
                .build();

        s3Client.putObject(putRequest, 
            software.amazon.awssdk.core.sync.RequestBody.fromBytes(jsonCatalog.getBytes(StandardCharsets.UTF_8)));
    }

}
