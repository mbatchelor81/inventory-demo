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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setSku("TEST-001");
        testProduct.setPrice(new BigDecimal("99.99"));

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Test Product 2");
        testProduct2.setDescription("Test Description 2");
        testProduct2.setSku("TEST-002");
        testProduct2.setPrice(new BigDecimal("149.99"));
    }

    @Test
    void getAllProducts_ShouldReturnProductList_WhenProductsExist() throws Exception {
        List<Product> products = Arrays.asList(testProduct, testProduct2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].description", is("Test Description")))
                .andExpect(jsonPath("$[0].sku", is("TEST-001")))
                .andExpect(jsonPath("$[0].price", is(99.99)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test Product 2")))
                .andExpect(jsonPath("$[1].description", is("Test Description 2")))
                .andExpect(jsonPath("$[1].sku", is("TEST-002")))
                .andExpect(jsonPath("$[1].price", is(149.99)));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(999L);
    }

    @Test
    void getProductBySku_ShouldReturnProduct_WhenProductExists() throws Exception {
        when(productService.getProductBySku("TEST-001")).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/sku/TEST-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService).getProductBySku("TEST-001");
    }

    @Test
    void getProductBySku_ShouldReturn404_WhenProductNotFound() throws Exception {
        when(productService.getProductBySku("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService).getProductBySku("NONEXISTENT");
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct_WhenValidProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setSku("NEW-001");
        newProduct.setPrice(new BigDecimal("199.99"));

        Product savedProduct = new Product();
        savedProduct.setId(3L);
        savedProduct.setName("New Product");
        savedProduct.setDescription("New Description");
        savedProduct.setSku("NEW-001");
        savedProduct.setPrice(new BigDecimal("199.99"));

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.sku", is("NEW-001")))
                .andExpect(jsonPath("$.price", is(199.99)));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenNameIsBlank() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("");
        invalidProduct.setDescription("Description");
        invalidProduct.setSku("SKU-001");
        invalidProduct.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuIsBlank() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("Product Name");
        invalidProduct.setDescription("Description");
        invalidProduct.setSku("");
        invalidProduct.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNull() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("Product Name");
        invalidProduct.setDescription("Description");
        invalidProduct.setSku("SKU-001");
        invalidProduct.setPrice(null);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenPriceIsNegative() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("Product Name");
        invalidProduct.setDescription("Description");
        invalidProduct.setSku("SKU-001");
        invalidProduct.setPrice(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuAlreadyExists() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setSku("EXISTING-SKU");
        newProduct.setPrice(new BigDecimal("199.99"));

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU EXISTING-SKU already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest());

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturn400_WhenMalformedJson() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenValidProduct() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setName("Updated Product");
        updateProduct.setDescription("Updated Description");
        updateProduct.setSku("UPDATED-001");
        updateProduct.setPrice(new BigDecimal("299.99"));

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setSku("UPDATED-001");
        updatedProduct.setPrice(new BigDecimal("299.99"));

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.sku", is("UPDATED-001")))
                .andExpect(jsonPath("$.price", is(299.99)));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setName("Updated Product");
        updateProduct.setDescription("Updated Description");
        updateProduct.setSku("UPDATED-001");
        updateProduct.setPrice(new BigDecimal("299.99"));

        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturn400_WhenValidationFails() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("");
        invalidProduct.setDescription("Description");
        invalidProduct.setSku("SKU-001");
        invalidProduct.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturn400_WhenSkuAlreadyExists() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setName("Updated Product");
        updateProduct.setDescription("Updated Description");
        updateProduct.setSku("EXISTING-SKU");
        updateProduct.setPrice(new BigDecimal("299.99"));

        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU EXISTING-SKU already exists"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void deleteProduct_ShouldReturn204_WhenProductExists() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(999L);
    }

}
