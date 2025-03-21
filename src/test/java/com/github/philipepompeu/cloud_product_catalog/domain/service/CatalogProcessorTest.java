package com.github.philipepompeu.cloud_product_catalog.domain.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.philipepompeu.cloud_product_catalog.domain.model.CategoryEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.model.ProductEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.CategoryRepository;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.ProductRepository;
import com.github.philipepompeu.cloud_product_catalog.infra.storage.StorageService;

@SpringBootTest
@DisplayName("Unit test of CatalogProcessor")
public class CatalogProcessorTest {
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private StorageService storageService;

    @InjectMocks
    private CatalogProcessor catalogProcessor;


    @Test
    @DisplayName("Should generate a new json catalog")
    void shouldGenerateJsonCatalog(){
        String ownerId = "97bfa736-90ae-4015-bdfa-c89a680a2f87";

        var category = new CategoryEntity();
        category.setId("123");
        category.setOwnerId(ownerId);
        category.setDescription("Description");
        category.setTitle("Category");       

        when(categoryRepository.findByOwnerId(eq(ownerId))).thenReturn(Optional.of(List.of(category)));

        var product1 = new ProductEntity();
        product1.setCategoryId(category.getId());
        product1.setId("123");
        product1.setOwnerId(ownerId);
        product1.setPrice(BigDecimal.valueOf(100));
        product1.setTitle("Product 1");
        product1.setDescription("Product 1");
        
        var product2 = new ProductEntity();
        product2.setCategoryId(category.getId());
        product2.setId("124");
        product2.setOwnerId(ownerId);
        product2.setPrice(BigDecimal.valueOf(100));
        product2.setTitle("Product 2");
        product2.setDescription("Product 2");

        when(productRepository.findByCategoryId(eq(category.getId()))).thenReturn(Optional.of(List.of(product1, product2)));

        catalogProcessor.process(ownerId);

        verify(storageService, times(1)).uploadJsonCatalogToTheCloud(eq(ownerId), anyString());

    }

}
