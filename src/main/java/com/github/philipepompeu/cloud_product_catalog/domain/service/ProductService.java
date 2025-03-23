package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;
import com.github.philipepompeu.cloud_product_catalog.domain.model.ProductEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.observer.CatalogEventPublisher;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;
    
    private final CatalogEventPublisher catalogEventPublisher;
    private final OwnerIdManager ownerIdManager;

    ProductService(ProductRepository repository, CatalogEventPublisher catalogEventPublisher, OwnerIdManager ownerIdManager){
        this.repository = repository;
        
        this.catalogEventPublisher = catalogEventPublisher;
        this.ownerIdManager = ownerIdManager;
    }

    public ProductDto createProduct(ProductDto dto){

        ProductEntity entity = ProductDto.toEntity(dto);

        entity.setCategoryId(dto.getCategoryId());
        
        entity.setId(null);
        entity.setOwnerId(ownerIdManager.getOwnerId());

        entity = this.repository.save(entity);
        
        catalogEventPublisher.notifyListeners(entity.getOwnerId());

        return ProductEntity.toDTO(entity);
    }

    public ProductDto updateProduct(ProductDto dto) throws Exception{

        ProductEntity entity = repository.findById(dto.getId()).orElseThrow( ()-> new Exception("Product not found"));

        entity.setPrice(dto.getPrice());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCategoryId(dto.getCategoryId());

        entity = repository.save(entity);

        catalogEventPublisher.notifyListeners(entity.getOwnerId());
        
        return ProductEntity.toDTO( entity );

    }

    public ProductDto deleteProductById(String id) throws Exception{

        ProductEntity entity = repository.findById(id).orElseThrow( ()-> new Exception("Product not found"));

        repository.delete(entity);

        catalogEventPublisher.notifyListeners(entity.getOwnerId());
        
        return ProductEntity.toDTO(entity);
    }

    public List<ProductDto> getAllProducts(){

        return this.repository.findAll().stream().map(entity -> ProductEntity.toDTO(entity)).toList();
    }

}
