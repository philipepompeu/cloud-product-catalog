package com.github.philipepompeu.cloud_product_catalog.domain.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;

import lombok.Data;

@Data
@Document(collection = "categories")
public class CategoryEntity {
    
    @Id
    private String id; // No MongoDB, IDs são Strings (ObjectId)
    
    private String title;
    private String description;
    private String ownerId;

    // Inicializa o ID automaticamente se for um novo objeto
    public CategoryEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public static CategoryEntity fromDTO(CategoryDto dto){
        CategoryEntity entity = new CategoryEntity();

        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setOwnerId(dto.getOwnerId());

        if (!(dto.getId() == null || dto.getId().isEmpty())) {
            entity.setId(dto.getId());            
        }
        
        return entity;
    }

}
