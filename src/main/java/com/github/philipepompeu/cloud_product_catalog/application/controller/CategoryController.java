package com.github.philipepompeu.cloud_product_catalog.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/categories")
public class CategoryController {


    @PostMapping
    @Operation(summary = "Create a new category")
    @ApiResponse(responseCode = "200", description = "New category created.")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto category){

        return ResponseEntity.ok().body(category);
    }

}
