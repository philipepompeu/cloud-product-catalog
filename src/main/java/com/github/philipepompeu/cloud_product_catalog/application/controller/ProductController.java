package com.github.philipepompeu.cloud_product_catalog.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/products")
public class ProductController {


    @PostMapping()
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "200", description = "New product created.")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product){

        return ResponseEntity.ok().body(new ProductDto());
    }

}
