package com.github.philipepompeu.cloud_product_catalog.domain.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;
    private String username;
    private String password;
    private Set<String> roles;

}
