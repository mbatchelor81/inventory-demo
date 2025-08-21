package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_ShouldReturnProductList_AndSerializeCorrectly() throws Exception {
        Product product1 = new Product("Test Product 1", "Description 1", "SKU001", new BigDecimal("19.99"));
        product1.setId(1L);
        Product product2 = new Product("Test Product 2", "Description 2", "SKU002", new BigDecimal("29.99"));
        product2.setId(2L);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].sku").value("SKU001"))
                .andExpect(jsonPath("$[0].price").value(19.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Product 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].sku").value("SKU002"))
                .andExpect(jsonPath("$[1].price").value(29.99));
    }

    @Test
    void getProductById_ShouldReturnProduct_AndSerializeCorrectly() throws Exception {
        Product product = new Product("Test Product", "Test Description", "SKU123", new BigDecimal("25.50"));
        product.setId(1L);

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.sku").value("SKU123"))
                .andExpect(jsonPath("$.price").value(25.50));
    }

    @Test
    void getProductById_WhenNotFound_ShouldReturn404() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductBySku_WhenNotFound_ShouldReturn404() throws Exception {
        when(productService.getProductBySku("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_WithValidData_ShouldDeserializeAndReturnCreated() throws Exception {
        Product inputProduct = new Product("New Product", "New Description", "NEWSKU", new BigDecimal("15.99"));
        Product savedProduct = new Product("New Product", "New Description", "NEWSKU", new BigDecimal("15.99"));
        savedProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        String productJson = objectMapper.writeValueAsString(inputProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.sku").value("NEWSKU"))
                .andExpect(jsonPath("$.price").value(15.99));
    }

    @Test
    void createProduct_WithMissingName_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"description\":\"Test\",\"sku\":\"SKU123\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithBlankName_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"\",\"description\":\"Test\",\"sku\":\"SKU123\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithMissingSku_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"Test Product\",\"description\":\"Test\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithBlankSku_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"Test Product\",\"description\":\"Test\",\"sku\":\"\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithMissingPrice_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"Test Product\",\"description\":\"Test\",\"sku\":\"SKU123\"}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithNegativePrice_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"Test Product\",\"description\":\"Test\",\"sku\":\"SKU123\",\"price\":-5.99}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithZeroPrice_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"Test Product\",\"description\":\"Test\",\"sku\":\"SKU123\",\"price\":0}";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithDuplicateSku_ShouldReturn400() throws Exception {
        Product product = new Product("Test Product", "Test Description", "DUPLICATE", new BigDecimal("19.99"));
        String productJson = objectMapper.writeValueAsString(product);

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DUPLICATE already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_WithValidData_ShouldDeserializeAndReturnUpdated() throws Exception {
        Product updatedProduct = new Product("Updated Product", "Updated Description", "UPDATED", new BigDecimal("35.99"));
        updatedProduct.setId(1L);

        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        String productJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.sku").value("UPDATED"))
                .andExpect(jsonPath("$.price").value(35.99));
    }

    @Test
    void updateProduct_WhenNotFound_ShouldReturn404() throws Exception {
        Product product = new Product("Test Product", "Test Description", "SKU123", new BigDecimal("19.99"));
        String productJson = objectMapper.writeValueAsString(product);

        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_WithInvalidData_ShouldReturn400() throws Exception {
        String invalidProductJson = "{\"name\":\"\",\"description\":\"Test\",\"sku\":\"SKU123\",\"price\":19.99}";

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_WithComplexBigDecimalPrice_ShouldSerializeCorrectly() throws Exception {
        Product inputProduct = new Product("Precision Product", "High precision price", "PRECISION", new BigDecimal("123.456789"));
        Product savedProduct = new Product("Precision Product", "High precision price", "PRECISION", new BigDecimal("123.456789"));
        savedProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        String productJson = objectMapper.writeValueAsString(inputProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(123.456789));
    }

    @Test
    void createProduct_WithNullDescription_ShouldAllowNullAndSerializeCorrectly() throws Exception {
        Product inputProduct = new Product("No Description Product", null, "NODESC", new BigDecimal("10.00"));
        Product savedProduct = new Product("No Description Product", null, "NODESC", new BigDecimal("10.00"));
        savedProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        String productJson = objectMapper.writeValueAsString(inputProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").doesNotExist());
    }
}
