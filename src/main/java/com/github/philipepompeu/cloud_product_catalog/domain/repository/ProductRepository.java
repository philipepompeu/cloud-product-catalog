package com.github.philipepompeu.cloud_product_catalog.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.philipepompeu.cloud_product_catalog.domain.model.ProductEntity;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    
    Optional<List<ProductEntity>> findByCategoryId(String categoryId);

}
