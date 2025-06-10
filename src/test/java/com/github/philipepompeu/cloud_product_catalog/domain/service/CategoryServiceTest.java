package com.github.philipepompeu.cloud_product_catalog.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.domain.observer.CatalogEventPublisher;
import com.github.philipepompeu.cloud_product_catalog.domain.repository.CategoryRepository;

@DataMongoTest
@DisplayName("Unit test of the CategoryService")
public class CategoryServiceTest {

    private final String ownerIdMock = "97bfa736-90ae-4015-bdfa-c89a680a2f87";

    @Autowired
    private CategoryRepository repository;
    
    @Mock
    private CatalogEventPublisher catalogEventPublisher;

    @Mock
    private OwnerIdManager ownerIdManager;    
    
    private CategoryService categoryService;

    @BeforeEach
    public void BeforeEach(){
        categoryService = new CategoryService(repository, catalogEventPublisher, ownerIdManager);
        when(ownerIdManager.getOwnerId()).thenReturn(ownerIdMock);
    }


    @Test
    @DisplayName("should save a new category and notify catologEventPublisher")
    void shouldCreateANewCategory(){        

        var category = new CategoryDto();
        category.setId("123");        
        category.setDescription("Description");
        category.setTitle("Category");        
        
        var savedCategory = categoryService.saveCategory(category);

        verify(catalogEventPublisher, times(1)).notifyListeners(eq(this.ownerIdMock));

        assertNotEquals(category.getId(), savedCategory.getId());//método saveCategory deve ignorar o ID enviado
        
        assertFalse(savedCategory.getId().isEmpty());
    }
    
    @Test
    @DisplayName("should update a existing category and notify catologEventPublisher")
    void shouldUpdateAExistingCategory() throws Exception{        

        var category = new CategoryDto();
        category.setId("123");        
        category.setDescription("Description");
        category.setTitle("Category");        
        
        var savedCategory = categoryService.saveCategory(category);

        savedCategory.setDescription("Updated description");

        savedCategory = categoryService.updateCategory(savedCategory);

        
        assertNotEquals(category.getId(), savedCategory.getId());//método saveCategory deve ignorar o ID enviado
        
        assertFalse(savedCategory.getId().isEmpty());
        assertEquals(savedCategory.getDescription(), "Updated description");

        verify(catalogEventPublisher, times(2)).notifyListeners(eq(this.ownerIdMock));
    }
    
    @Test
    @DisplayName("should delete a existing category and notify catologEventPublisher")
    void shouldDeleteAExistingCategory() throws Exception{        

        var category = new CategoryDto();
        category.setId("123");        
        category.setDescription("Description");
        category.setTitle("Category");        
        
        var deletedCategory = categoryService.deleteCategory(categoryService.saveCategory(category).getId());        
        
        assertFalse(deletedCategory.getId().isEmpty());

        verify(catalogEventPublisher, times(2)).notifyListeners(eq(this.ownerIdMock)); //1x pra criação e 1x para deleção
    }
    
}
