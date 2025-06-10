package com.github.philipepompeu.cloud_product_catalog.infra.storage;

public interface StorageService {


    void uploadJsonCatalogToTheCloud(String ownerId, String jsonCatalog);

}
