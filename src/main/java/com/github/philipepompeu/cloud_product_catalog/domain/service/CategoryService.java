package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.domain.model.CategoryEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    
    CategoryService(CategoryRepository repository){
        this.repository = repository;
    }

    public CategoryDto saveCategory(CategoryDto dto){
        dto.setId(null);

        CategoryEntity entity = CategoryEntity.fromDTO(dto);

        entity = this.repository.save(entity);

        return CategoryDto.fromEntity(entity);

    }

    public List<CategoryDto> getAllCategories(){
        
        return this.repository.findAll().stream().map(entity -> CategoryDto.fromEntity(entity)).toList();
    }

    public CategoryDto updateCategory(CategoryDto dto) throws Exception{

        CategoryEntity entity = this.repository.findById(dto.getId()).orElseThrow(()-> new Exception("Could not find category"));

        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());

        return CategoryDto.fromEntity(this.repository.save(entity));
    }

    public CategoryDto deleteCategory(String categoryId) throws Exception{
        CategoryEntity entity = this.repository.findById(categoryId).orElseThrow(()-> new Exception("Could not find category"));

        this.repository.delete(entity);

        return CategoryDto.fromEntity(entity);

    }

}
