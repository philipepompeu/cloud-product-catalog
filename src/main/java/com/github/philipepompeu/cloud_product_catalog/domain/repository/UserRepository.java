package com.github.philipepompeu.cloud_product_catalog.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.philipepompeu.cloud_product_catalog.domain.model.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {

}
