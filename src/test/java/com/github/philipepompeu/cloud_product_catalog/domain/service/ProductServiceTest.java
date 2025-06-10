package com.github.philipepompeu.cloud_product_catalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;
import com.github.philipepompeu.cloud_product_catalog.domain.observer.CatalogEventPublisher;

import com.github.philipepompeu.cloud_product_catalog.domain.repository.ProductRepository;

@DataMongoTest
@DisplayName("Unit test of the ProductService")
public class ProductServiceTest {
    
    
   private final String ownerIdMock = "97bfa736-90ae-4015-bdfa-c89a680a2f87";

    @Autowired
    private ProductRepository repository;
    
    @Mock
    private CatalogEventPublisher catalogEventPublisher;

    @Mock
    private OwnerIdManager ownerIdManager;    
    
    private ProductService productService;

    @BeforeEach
    public void BeforeEach(){
        productService = new ProductService(repository, catalogEventPublisher, ownerIdManager);
        when(ownerIdManager.getOwnerId()).thenReturn(ownerIdMock);
    }


    @Test
    @DisplayName("should save a new product and notify catologEventPublisher")
    void shouldCreateANewProduct(){        

        var product = new ProductDto();
        product.setId("123");    
        product.setCategoryId("category-id");  
        product.setPrice(BigDecimal.valueOf(1000));  
        product.setDescription("Description");
        product.setTitle("product");        
        
        var savedProduct = productService.createProduct(product);

        verify(catalogEventPublisher, times(1)).notifyListeners(eq(this.ownerIdMock));

        assertNotEquals(product.getId(), savedProduct.getId());//método saveproduct deve ignorar o ID enviado
        
        assertEquals(savedProduct.getOwnerId(), this.ownerIdMock);
        assertFalse(savedProduct.getId().isEmpty());
    }
    
    @Test
    @DisplayName("should update a existing product and notify catologEventPublisher")
    void shouldUpdateAExistingProduct() throws Exception{        

        var product = new ProductDto();
        product.setId("123");
        product.setCategoryId("category-id");  
        product.setPrice(BigDecimal.valueOf(1000));  
        product.setDescription("Description");
        product.setTitle("product");        
        
        var savedProduct = productService.createProduct(product);

        savedProduct.setPrice(BigDecimal.valueOf(1100));

        savedProduct = productService.updateProduct(savedProduct);        
        
        
        assertFalse(savedProduct.getId().isEmpty());
        assertEquals(savedProduct.getPrice(), BigDecimal.valueOf(1100));

        verify(catalogEventPublisher, times(2)).notifyListeners(eq(this.ownerIdMock));
    }
    
    @Test
    @DisplayName("should delete a existing product and notify catologEventPublisher")
    void shouldDeleteAExistingProduct() throws Exception{        

        var product = new ProductDto();
        product.setId("123");
        product.setCategoryId("category-id");  
        product.setPrice(BigDecimal.valueOf(1000));  
        product.setDescription("Description");
        product.setTitle("product");        
        
        var deletedproduct = productService.deleteProductById(productService.createProduct(product).getId());        
        
        assertFalse(deletedproduct.getId().isEmpty());

        verify(catalogEventPublisher, times(2)).notifyListeners(eq(this.ownerIdMock)); //1x pra criação e 1x para deleção
    }


}
