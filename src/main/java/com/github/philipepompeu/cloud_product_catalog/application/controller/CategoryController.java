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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.domain.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/categories")
@Tag(name="Categories",
     description="List, create, delete and update categories of your catalog.")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    @ApiResponse(responseCode = "200", description = "New category created.")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto category){        
        return ResponseEntity.ok().body( categoryService.saveCategory(category) );
    }

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<List<CategoryDto>> listCategories(){
        return ResponseEntity.ok().body( categoryService.getAllCategories() );
    }

    @PutMapping
    @Operation(summary = "Update a category")
    @ApiResponse(responseCode = "200", description = "Category updated.")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto category){

        try {
            return ResponseEntity.ok().body(categoryService.updateCategory(category));    
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category of the given id")
    @ApiResponse(responseCode = "200", description = "Category deleted.")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable(name="id") String categoryId){

        try {
            return ResponseEntity.ok().body(categoryService.deleteCategory(categoryId));    
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

}
