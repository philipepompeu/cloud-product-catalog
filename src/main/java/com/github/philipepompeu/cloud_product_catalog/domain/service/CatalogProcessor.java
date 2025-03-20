package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philipepompeu.cloud_product_catalog.application.dto.CatalogDto;
import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.application.dto.JsonViews;
import com.github.philipepompeu.cloud_product_catalog.domain.model.ProductEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.CategoryRepository;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.ProductRepository;
import com.github.philipepompeu.cloud_product_catalog.infra.storage.StorageService;

@Service
public class CatalogProcessor {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    CatalogProcessor(CategoryRepository categoryRepository, ProductRepository productRepository, StorageService storageService){
        
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.storageService = storageService;

    }
    
    public void process(String ownerId){

        List<CategoryDto> categories = categoryRepository.findByOwnerId(ownerId)
                                        .orElse(List.of())
                                        .stream()                                        
                                        .map((entity)-> CategoryDto.fromEntity(entity) )
                                        .peek(dto -> {
                                            var products = productRepository.findByCategoryId(dto.getId()).orElse(List.of());

                                            if (!products.isEmpty()) {
                                                System.out.println("Tem produtos");
                                                dto.setProducts(products.stream()
                                                    .map(ProductEntity::toDTO) // Converter produtos
                                                    .toList());                                                
                                            } else {
                                                System.out.println("Nenhum produto encontrado para a categoria: " + dto.getId());
                                            }

                                        })  
                                        .toList();      

        if (categories.size() > 0) {
            CatalogDto catalog = new CatalogDto(ownerId,categories);
            try {              

                String jsonCatalog = objectMapper.writerWithView(JsonViews.Catalog.class).writeValueAsString(catalog);

                storageService.uploadJsonCatalogToTheCloud(ownerId, jsonCatalog);
                
            } catch (Exception e) {
                System.out.println("Failed to convert object catalog.");
            }
        }
    }
}
