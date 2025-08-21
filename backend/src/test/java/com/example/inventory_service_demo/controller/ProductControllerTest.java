package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_ShouldReturnProductList_WhenProductsExist() throws Exception {
        List<Product> products = Arrays.asList(
                createValidProduct(1L, "Product 1", "Description 1", "SKU001", new BigDecimal("10.99")),
                createValidProduct(2L, "Product 2", "Description 2", "SKU002", new BigDecimal("20.50"))
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].sku").value("SKU001"))
                .andExpect(jsonPath("$[0].price").value(10.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        Product product = createValidProduct(1L, "Test Product", "Test Description", "TEST001", new BigDecimal("15.99"));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.sku").value("TEST001"))
                .andExpect(jsonPath("$.price").value(15.99));
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductBySku_ShouldReturnProduct_WhenProductExists() throws Exception {
        Product product = createValidProduct(1L, "Test Product", "Test Description", "TEST001", new BigDecimal("15.99"));
        when(productService.getProductBySku("TEST001")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/sku/TEST001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.sku").value("TEST001"));
    }

    @Test
    void getProductBySku_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductBySku("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct_WhenValidProduct() throws Exception {
        Product inputProduct = createValidProduct(null, "New Product", "New Description", "NEW001", new BigDecimal("25.99"));
        Product savedProduct = createValidProduct(1L, "New Product", "New Description", "NEW001", new BigDecimal("25.99"));
        
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.sku").value("NEW001"))
                .andExpect(jsonPath("$.price").value(25.99));
    }

    @Test
    void createProduct_ShouldReturn400_WhenNameIsBlank() throws Exception {
        Product invalidProduct = createValidProduct(null, "", "Description", "SKU001", new BigDecimal("10.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenNameIsNull() throws Exception {
        Product invalidProduct = createValidProduct(null, null, "Description", "SKU001", new BigDecimal("10.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuIsBlank() throws Exception {
        Product invalidProduct = createValidProduct(null, "Product Name", "Description", "", new BigDecimal("10.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuIsNull() throws Exception {
        Product invalidProduct = createValidProduct(null, "Product Name", "Description", null, new BigDecimal("10.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNull() throws Exception {
        Product invalidProduct = createValidProduct(null, "Product Name", "Description", "SKU001", null);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNegative() throws Exception {
        Product invalidProduct = createValidProduct(null, "Product Name", "Description", "SKU001", new BigDecimal("-5.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsZero() throws Exception {
        Product invalidProduct = createValidProduct(null, "Product Name", "Description", "SKU001", BigDecimal.ZERO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenDuplicateSku() throws Exception {
        Product product = createValidProduct(null, "Product Name", "Description", "DUPLICATE", new BigDecimal("10.99"));
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DUPLICATE already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenInvalidJsonStructure() throws Exception {
        String invalidJson = "{\"name\":\"Product\",\"price\":\"invalid_number\"}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenMalformedJson() throws Exception {
        String malformedJson = "{\"name\":\"Product\",\"price\":10.99";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenValidProduct() throws Exception {
        Product inputProduct = createValidProduct(null, "Updated Product", "Updated Description", "UPD001", new BigDecimal("30.99"));
        Product updatedProduct = createValidProduct(1L, "Updated Product", "Updated Description", "UPD001", new BigDecimal("30.99"));
        
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.sku").value("UPD001"))
                .andExpect(jsonPath("$.price").value(30.99));
    }

    @Test
    void updateProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        Product product = createValidProduct(null, "Product Name", "Description", "SKU001", new BigDecimal("10.99"));
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_ShouldReturn400_WhenValidationFails() throws Exception {
        Product invalidProduct = createValidProduct(null, "", "Description", "SKU001", new BigDecimal("10.99"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_ShouldReturn204_WhenProductExists() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void createProduct_ShouldHandleBigDecimalPrecision() throws Exception {
        Product inputProduct = createValidProduct(null, "Precision Product", "Test precision", "PREC001", new BigDecimal("123.456789"));
        Product savedProduct = createValidProduct(1L, "Precision Product", "Test precision", "PREC001", new BigDecimal("123.456789"));
        
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(123.456789));
    }

    @Test
    void createProduct_ShouldAllowNullDescription() throws Exception {
        Product inputProduct = createValidProduct(null, "Product Name", null, "SKU001", new BigDecimal("10.99"));
        Product savedProduct = createValidProduct(1L, "Product Name", null, "SKU001", new BigDecimal("10.99"));
        
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    private Product createValidProduct(Long id, String name, String description, String sku, BigDecimal price) {
        Product product = new Product(name, description, sku, price);
        if (id != null) {
            product.setId(id);
        }
        return product;
    }
}
