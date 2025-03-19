package com.github.philipepompeu.cloud_product_catalog.domain.observer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.philipepompeu.cloud_product_catalog.infra.messaging.SQSPublisher;

@Component
public class CatalogEventPublisher {

    private final List<CatalogEventListener> listeners = new ArrayList<>();

    // Injetamos o SQSPublisher e o registramos automaticamente
    public CatalogEventPublisher(SQSPublisher sqsPublisher) {
        addListener(sqsPublisher);
    }

    public void addListener(CatalogEventListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(String ownerId) {
        for (CatalogEventListener listener : listeners) {
            listener.onCatalogUpdated(ownerId);
        }
    }

}
