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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product testProduct;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct = createTestProduct(1L, "Test Product", "A test product", "TEST-001", new BigDecimal("99.99"));
        testProduct2 = createTestProduct(2L, "Test Product 2", "Another test product", "TEST-002", new BigDecimal("149.99"));
    }

    private Product createTestProduct(Long id, String name, String description, String sku, BigDecimal price) {
        Product product = new Product(name, description, sku, price);
        product.setId(id);
        return product;
    }

    @Test
    void getAllProducts_ShouldReturnProductListJson() throws Exception {
        List<Product> products = Arrays.asList(testProduct, testProduct2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].description", is("A test product")))
                .andExpect(jsonPath("$[0].sku", is("TEST-001")))
                .andExpect(jsonPath("$[0].price", is(99.99)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test Product 2")))
                .andExpect(jsonPath("$[1].description", is("Another test product")))
                .andExpect(jsonPath("$[1].sku", is("TEST-002")))
                .andExpect(jsonPath("$[1].price", is(149.99)));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_EmptyList_ShouldReturnEmptyJsonArray() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ExistingProduct_ShouldReturnProductJson() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("A test product")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_NonExistentProduct_ShouldReturn404() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(999L);
    }

    @Test
    void getProductBySku_ExistingProduct_ShouldReturnProductJson() throws Exception {
        when(productService.getProductBySku("TEST-001")).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/sku/TEST-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("A test product")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService).getProductBySku("TEST-001");
    }

    @Test
    void getProductBySku_NonExistentProduct_ShouldReturn404() throws Exception {
        when(productService.getProductBySku("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT"))
                .andExpect(status().isNotFound());

        verify(productService).getProductBySku("NONEXISTENT");
    }

    @Test
    void createProduct_ValidProduct_ShouldReturnCreatedProductJson() throws Exception {
        Product newProduct = createTestProduct(null, "New Product", "A new product", "NEW-001", new BigDecimal("199.99"));
        Product createdProduct = createTestProduct(3L, "New Product", "A new product", "NEW-001", new BigDecimal("199.99"));
        
        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        String productJson = objectMapper.writeValueAsString(newProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("A new product")))
                .andExpect(jsonPath("$.sku", is("NEW-001")))
                .andExpect(jsonPath("$.price", is(199.99)));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_MissingRequiredFields_ShouldReturn400() throws Exception {
        Product invalidProduct = new Product();

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_BlankName_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "", "Description", "SKU-001", new BigDecimal("99.99"));

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_BlankSku_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "Product Name", "Description", "", new BigDecimal("99.99"));

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_NullPrice_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "Product Name", "Description", "SKU-001", null);

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_NegativePrice_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "Product Name", "Description", "SKU-001", new BigDecimal("-10.00"));

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ZeroPrice_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "Product Name", "Description", "SKU-001", BigDecimal.ZERO);

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void createProduct_DuplicateSku_ShouldReturn400() throws Exception {
        Product newProduct = createTestProduct(null, "New Product", "A new product", "DUPLICATE-SKU", new BigDecimal("199.99"));
        
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DUPLICATE-SKU already exists"));

        String productJson = objectMapper.writeValueAsString(newProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_InvalidJson_ShouldReturn400() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_ValidProduct_ShouldReturnUpdatedProductJson() throws Exception {
        Product updatedProduct = createTestProduct(1L, "Updated Product", "Updated description", "UPDATED-001", new BigDecimal("299.99"));
        
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        String productJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.sku", is("UPDATED-001")))
                .andExpect(jsonPath("$.price", is(299.99)));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_NonExistentProduct_ShouldReturn404() throws Exception {
        Product updateProduct = createTestProduct(null, "Updated Product", "Updated description", "UPDATED-001", new BigDecimal("299.99"));
        
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        String productJson = objectMapper.writeValueAsString(updateProduct);

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void updateProduct_InvalidValidation_ShouldReturn400() throws Exception {
        Product invalidProduct = createTestProduct(null, "", "Description", "", null);

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    void updateProduct_DuplicateSku_ShouldReturn404() throws Exception {
        Product updateProduct = createTestProduct(null, "Updated Product", "Updated description", "DUPLICATE-SKU", new BigDecimal("299.99"));
        
        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DUPLICATE-SKU already exists"));

        String productJson = objectMapper.writeValueAsString(updateProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void deleteProduct_ExistingProduct_ShouldReturn204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_NonExistentProduct_ShouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(999L);
    }

}
