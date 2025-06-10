package com.github.philipepompeu.cloud_product_catalog.domain.observer;

public interface CatalogEventListener {

    void onCatalogUpdated(String ownerId);

}
