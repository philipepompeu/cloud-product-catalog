package com.github.philipepompeu.cloud_product_catalog.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogDto {

    private List<CategoryDto> categories;
    
}
