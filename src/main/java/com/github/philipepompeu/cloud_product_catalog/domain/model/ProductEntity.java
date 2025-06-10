package com.github.philipepompeu.cloud_product_catalog.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;

import lombok.Data;

@Data
@Document(collection = "products")
public class ProductEntity {

    @Id
    private String id;
    
    private String title;
    private String description;
    private BigDecimal price;
    private String categoryId;    
    private String ownerId;
    
    public ProductEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public static ProductDto toDTO(ProductEntity entity){   
        ProductDto dto = new ProductDto();
        
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription() );
        dto.setPrice(entity.getPrice() );
        dto.setCategoryId(entity.getCategoryId());
        dto.setOwnerId(entity.getOwnerId());
        dto.setId(entity.getId());
        
        return dto;
    }

}
