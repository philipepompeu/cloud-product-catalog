package com.github.philipepompeu.cloud_product_catalog.application.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.philipepompeu.cloud_product_catalog.domain.model.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String title;
    private String description;
    private BigDecimal price;
    private String categoryId;
    @JsonIgnore
    private String ownerId;
    private String id;

    public static ProductEntity toEntity(ProductDto dto){
        ProductEntity entity = new ProductEntity();

        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setPrice(dto.getPrice());
        entity.setId(dto.getId());
        entity.setOwnerId(dto.getOwnerId());

        return entity;
    }
}
