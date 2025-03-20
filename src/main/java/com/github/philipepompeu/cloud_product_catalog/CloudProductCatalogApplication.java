package com.github.philipepompeu.cloud_product_catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API for Cloud Product Catalog.", version = "1.0"))
public class CloudProductCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudProductCatalogApplication.class, args);
	}

}
