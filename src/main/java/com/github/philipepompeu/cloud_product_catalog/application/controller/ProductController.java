package com.github.philipepompeu.cloud_product_catalog.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;
import com.github.philipepompeu.cloud_product_catalog.domain.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/products")
@Tag(name="Products",
     description="List, create, delete and update products of your catalog.")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping()
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "200", description = "New product created.")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product){
        
        return ResponseEntity.ok().body( productService.createProduct(product) );
    }

    @GetMapping
    @Operation(summary = "List all products of the catalog")
    @ApiResponse(responseCode ="200", description = "a list of the products of your catalog")
    public ResponseEntity<List<ProductDto>> listAllProducts(){

        return ResponseEntity.ok().body(productService.getAllProducts());
        
    }

    @PutMapping
    @Operation(summary = "Update a existing product")
    @ApiResponse(responseCode = "200", description = "update properties of a existing product")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto product){

        try {
            return ResponseEntity.ok().body(productService.updateProduct(product));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable() String id){
        try {
            return ResponseEntity.ok().body(productService.deleteProductById(id));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

}
