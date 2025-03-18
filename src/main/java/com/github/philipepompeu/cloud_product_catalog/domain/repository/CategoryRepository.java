package com.github.philipepompeu.cloud_product_catalog.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.philipepompeu.cloud_product_catalog.domain.model.CategoryEntity;

@Repository
public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {

}
