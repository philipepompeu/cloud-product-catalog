package com.github.philipepompeu.cloud_product_catalog.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test E2E of the endpoint /categories")
public class CategoryControllerTest {     

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    public void BeforeEach(){
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private ResponseEntity<CategoryDto> execRequest(CategoryDto dto, HttpMethod endPointMethod ){        
    
        return restTemplate.exchange(
            "/categories",
            endPointMethod,
            new HttpEntity<>(dto, headers),
            CategoryDto.class );

    }
    
    private ResponseEntity<List<CategoryDto>> getAll(){        
    
        return restTemplate.exchange(
            "/categories",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CategoryDto>>() {} );

    }

    private ResponseEntity<CategoryDto> post(CategoryDto dto){
        return execRequest(dto, HttpMethod.POST);
    }
    
    private ResponseEntity<CategoryDto> put(CategoryDto dto){
        return execRequest(dto, HttpMethod.PUT);
    }
    
    private ResponseEntity<CategoryDto> delete(CategoryDto dto){

        String url = "/categories/"+dto.getId();
        
        return restTemplate.exchange(url, HttpMethod.DELETE, null, CategoryDto.class);        
    }    

    @Test
    @DisplayName("should responde <200> after creating a new category")
    void shouldCreateANewCategory(){

        CategoryDto dto = new CategoryDto();

        dto.setDescription("New Category");
        dto.setTitle("Category");        

        ResponseEntity<CategoryDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        
    }

    @Test
    @DisplayName("should responde <200> after updating the category")
    void shouldUpdateTheCategory(){

        CategoryDto dto = new CategoryDto();

        dto.setDescription("New Category");
        dto.setTitle("Category");        

        ResponseEntity<CategoryDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        CategoryDto update = response.getBody();
        if (!update.getId().isEmpty()) {
            update.setDescription("Description updated.");

            response = this.put(update);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDescription()).isEqualTo("Description updated.");
        }
    }
    
    @Test
    @DisplayName("should responde <200> after delete the category")
    void shouldDeleteTheCategory(){

        CategoryDto dto = new CategoryDto();

        dto.setDescription("New Category");
        dto.setTitle("Category");        

        ResponseEntity<CategoryDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        CategoryDto toBeDeleted = response.getBody();
        if (!toBeDeleted.getId().isEmpty()) {           

            ResponseEntity<CategoryDto> deleteResponse = this.delete(toBeDeleted);            

            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(deleteResponse.getBody()).isNotNull();
            assertThat(deleteResponse.getBody().getId()).isEqualTo(toBeDeleted.getId());

        }
    }
    
    @Test
    @DisplayName("should responde <200> with a list of categories")    
    void shouldListAllTheCategories(){

        CategoryDto dto = new CategoryDto();

        dto.setDescription("New Category");
        dto.setTitle("Category");        

        ResponseEntity<CategoryDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        CategoryDto shouldBeInTheList = response.getBody();
        if (!shouldBeInTheList.getId().isEmpty()) {           

            ResponseEntity<List<CategoryDto>> categories = this.getAll();

            assertThat(categories.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(categories.getBody()).isNotNull();

            assertThat(categories.getBody()).anyMatch(category -> shouldBeInTheList.getId().equals(category.getId()));

        }
    }
}
