package com.github.philipepompeu.cloud_product_catalog.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.github.philipepompeu.cloud_product_catalog.application.dto.CategoryDto;
import com.github.philipepompeu.cloud_product_catalog.application.dto.ProductDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test E2E of the endpoint /products")
public class ProductControllerTest {


    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();
    private String categoryId = "";
    

    private String generateCategory() throws Exception{

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<CategoryDto> response = restTemplate.exchange(
            "/categories",
            HttpMethod.POST,
            new HttpEntity<>(new CategoryDto("", "Title", "Description", "", List.of()), headers),
            CategoryDto.class );
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getId();
        }else{
            throw new Exception("Could not generate category");
        }

    }

    
    @BeforeEach
    public void BeforeEach(){
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            this.categoryId = this.generateCategory();            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private ResponseEntity<ProductDto> execRequest(ProductDto dto, HttpMethod endPointMethod ){        
    
        return restTemplate.exchange(
            "/products",
            endPointMethod,
            new HttpEntity<>(dto, headers),
            ProductDto.class );

    }
    
    private ResponseEntity<List<ProductDto>> getAll(){        
    
        return restTemplate.exchange(
            "/products",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ProductDto>>() {} );

    }

    private ResponseEntity<ProductDto> post(ProductDto dto){
        return execRequest(dto, HttpMethod.POST);
    }
    
    private ResponseEntity<ProductDto> put(ProductDto dto){
        return execRequest(dto, HttpMethod.PUT);
    }
    
    private ResponseEntity<ProductDto> delete(ProductDto dto){

        String url = "/products/"+dto.getId();
        
        return restTemplate.exchange(url, HttpMethod.DELETE, null, ProductDto.class);        
    }    

    @Test
    @DisplayName("should responde <200> after creating a new product")
    void shouldCreateANewProduct(){

        ProductDto dto = new ProductDto();

        dto.setDescription("New Product");
        dto.setTitle("Product");        
        dto.setCategoryId(categoryId);
        dto.setPrice(BigDecimal.valueOf(100));        

        ResponseEntity<ProductDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getCategoryId()).isEqualTo(categoryId);
        
    }

    @Test
    @DisplayName("should responde <200> after updating the product")
    void shouldUpdateTheProduct(){

        ProductDto dto = new ProductDto();

        dto.setDescription("New Product");
        dto.setTitle("Product"); 
        dto.setPrice(BigDecimal.valueOf(1000));  
        dto.setCategoryId(categoryId);     

        ResponseEntity<ProductDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        ProductDto update = response.getBody();
        if (!update.getId().isEmpty()) {
            update.setDescription("Description updated.");
            update.setPrice(BigDecimal.valueOf(1100));

            response = this.put(update);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDescription()).isEqualTo("Description updated.");
            assertThat(response.getBody().getPrice()).isEqualTo(BigDecimal.valueOf(1100));
        }
    }
    
    @Test
    @DisplayName("should responde <200> after delete the product")
    void shouldDeleteTheProduct(){

        ProductDto dto = new ProductDto();

        dto.setDescription("New Product");
        dto.setTitle("Product");
        dto.setCategoryId(categoryId);        
        dto.setPrice(BigDecimal.valueOf(1000));

        ResponseEntity<ProductDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        ProductDto toBeDeleted = response.getBody();
        if (!toBeDeleted.getId().isEmpty()) {           

            ResponseEntity<ProductDto> deleteResponse = this.delete(toBeDeleted);            

            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(deleteResponse.getBody()).isNotNull();
            assertThat(deleteResponse.getBody().getId()).isEqualTo(toBeDeleted.getId());

        }
    }
    
    @Test
    @DisplayName("should responde <200> with a list of products")
    void shouldListAllTheProducts(){

        ProductDto dto = new ProductDto();

        dto.setDescription("New Product");
        dto.setTitle("Product");
        dto.setCategoryId(categoryId);        
        dto.setPrice(BigDecimal.valueOf(1000));

        ResponseEntity<ProductDto> response = this.post(dto);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        ProductDto shouldBeInTheList = response.getBody();
        if (!shouldBeInTheList.getId().isEmpty()) {           

            ResponseEntity<List<ProductDto>> products = this.getAll();

            assertThat(products.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(products.getBody()).isNotNull();

            assertThat(products.getBody()).anyMatch(product -> shouldBeInTheList.getId().equals(product.getId()));

        }
    }
    
}
