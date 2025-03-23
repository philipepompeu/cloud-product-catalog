package com.github.philipepompeu.cloud_product_catalog.application.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.philipepompeu.cloud_product_catalog.domain.model.CategoryEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @JsonView(JsonViews.Default.class)
    private String id;
    
    @JsonView(JsonViews.Default.class)
    private String title;
    @JsonView(JsonViews.Default.class)
    private String description;

    @JsonIgnore    
    private String ownerId;

    @JsonView(JsonViews.Catalog.class) // Apenas para o catálogo!
    private List<ProductDto> products;

    public static CategoryDto fromEntity(CategoryEntity entity){

        return new CategoryDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getOwnerId(), List.of());
    }
}
