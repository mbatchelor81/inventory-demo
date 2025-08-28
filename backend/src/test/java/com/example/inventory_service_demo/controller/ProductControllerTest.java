package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.*;
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

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Product testProductWithId;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", "TEST-SKU-001", new BigDecimal("99.99"));
        testProductWithId = new Product("Test Product", "Test Description", "TEST-SKU-001", new BigDecimal("99.99"));
        testProductWithId.setId(1L);
    }

    @Test
    void getAllProducts_ShouldReturnProductList_WhenProductsExist() throws Exception {
        List<Product> products = Arrays.asList(testProductWithId);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].description", is("Test Description")))
                .andExpect(jsonPath("$[0].sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$[0].price", is(99.99)));
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProductWithId));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductBySku_ShouldReturnProduct_WhenProductExists() throws Exception {
        when(productService.getProductBySku("TEST-SKU-001")).thenReturn(Optional.of(testProductWithId));

        mockMvc.perform(get("/api/products/sku/TEST-SKU-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void getProductBySku_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductBySku("NONEXISTENT-SKU")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct_WhenValidProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(testProductWithId);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void createProduct_ShouldReturn400_WhenNameIsBlank() throws Exception {
        Product invalidProduct = new Product("", "Test Description", "TEST-SKU-001", new BigDecimal("99.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuIsBlank() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "", new BigDecimal("99.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNull() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "TEST-SKU-001", null);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNegative() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "TEST-SKU-001", new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuAlreadyExists() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU TEST-SKU-001 already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldHandleNullDescription() throws Exception {
        Product productWithNullDescription = new Product("Test Product", null, "TEST-SKU-001", new BigDecimal("99.99"));
        Product createdProduct = new Product("Test Product", null, "TEST-SKU-001", new BigDecimal("99.99"));
        createdProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productWithNullDescription)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void createProduct_ShouldHandleBigDecimalPrecision() throws Exception {
        Product preciseProduct = new Product("Precise Product", "Test Description", "PRECISE-SKU", new BigDecimal("123.456789"));
        Product createdPreciseProduct = new Product("Precise Product", "Test Description", "PRECISE-SKU", new BigDecimal("123.456789"));
        createdPreciseProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(createdPreciseProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preciseProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.price", is(123.456789)));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenValidProduct() throws Exception {
        Product updatedProduct = new Product("Updated Product", "Updated Description", "UPDATED-SKU", new BigDecimal("199.99"));
        updatedProduct.setId(1L);

        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.sku", is("UPDATED-SKU")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test
    void updateProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_ShouldReturn400_WhenNameIsBlank() throws Exception {
        Product invalidProduct = new Product("", "Test Description", "TEST-SKU-001", new BigDecimal("99.99"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ShouldReturn400_WhenSkuIsBlank() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "", new BigDecimal("99.99"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ShouldReturn400_WhenPriceIsNull() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "TEST-SKU-001", null);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ShouldReturn400_WhenPriceIsNegative() throws Exception {
        Product invalidProduct = new Product("Test Product", "Test Description", "TEST-SKU-001", new BigDecimal("-10.00"));

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
    void createProduct_ShouldDeserializeJsonCorrectly() throws Exception {
        String jsonPayload = """
                {
                    "name": "JSON Test Product",
                    "description": "Product created from JSON",
                    "sku": "JSON-SKU-001",
                    "price": 149.99
                }
                """;

        Product expectedProduct = new Product("JSON Test Product", "Product created from JSON", "JSON-SKU-001", new BigDecimal("149.99"));
        expectedProduct.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(expectedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("JSON Test Product")))
                .andExpect(jsonPath("$.description", is("Product created from JSON")))
                .andExpect(jsonPath("$.sku", is("JSON-SKU-001")))
                .andExpect(jsonPath("$.price", is(149.99)));
    }

    @Test
    void createProduct_ShouldReturn400_WhenInvalidJsonFormat() throws Exception {
        String invalidJson = """
                {
                    "name": "Test Product",
                    "description": "Test Description",
                    "sku": "TEST-SKU-001",
                    "price": "invalid_price_format"
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_ShouldReturn400_WhenMalformedJson() throws Exception {
        String malformedJson = """
                {
                    "name": "Test Product",
                    "description": "Test Description",
                    "sku": "TEST-SKU-001",
                    "price": 99.99
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}
