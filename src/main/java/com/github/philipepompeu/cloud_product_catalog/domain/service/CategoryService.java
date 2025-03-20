package com.github.philipepompeu.cloud_product_catalog.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.domain.model.CategoryEntity;
import com.github.philipepompeu.cloud_product_catalog.domain.observer.CatalogEventPublisher;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final CatalogEventPublisher catalogEventPublisher;
    private final OwnerIdManager ownerIdManager;

    CategoryService(CategoryRepository repository, CatalogEventPublisher catalogEventPublisher, OwnerIdManager ownerIdManager){
        this.repository = repository;
        this.catalogEventPublisher = catalogEventPublisher;
        this.ownerIdManager = ownerIdManager;
    }

    public CategoryDto saveCategory(CategoryDto dto){
        dto.setId(null);

        CategoryEntity entity = CategoryEntity.fromDTO(dto);
        entity.setOwnerId(ownerIdManager.getOwnerId());

        entity = this.repository.save(entity);

        catalogEventPublisher.notifyListeners(entity.getOwnerId());

        return CategoryDto.fromEntity(entity);

    }

    public List<CategoryDto> getAllCategories(){
        
        return this.repository.findByOwnerId(ownerIdManager.getOwnerId()).orElse(List.of()).stream().map(entity -> CategoryDto.fromEntity(entity)).toList();
    }

    public CategoryDto updateCategory(CategoryDto dto) throws Exception{

        CategoryEntity entity = this.repository.findById(dto.getId()).orElseThrow(()-> new Exception("Could not find category"));

        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());

        entity = this.repository.save(entity);

        catalogEventPublisher.notifyListeners(entity.getOwnerId());

        return CategoryDto.fromEntity(entity);
    }

    public CategoryDto deleteCategory(String categoryId) throws Exception{
        CategoryEntity entity = this.repository.findById(categoryId).orElseThrow(()-> new Exception("Could not find category"));

        this.repository.delete(entity);

        catalogEventPublisher.notifyListeners(entity.getOwnerId());

        return CategoryDto.fromEntity(entity);

    }

    public CategoryEntity getCategoryEntityById(String id){

        return this.repository.findById(id).orElse(null);
    }

}
