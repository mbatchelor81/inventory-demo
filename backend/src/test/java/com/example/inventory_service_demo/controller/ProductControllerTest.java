package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.ProductService;
import com.example.inventory_service_demo.util.ProductTestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_ReturnEmptyList_When_NoProductsExist() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void should_ReturnProductList_When_ProductsExist() throws Exception {
        Product product1 = ProductTestDataBuilder.aProduct()
                .withName("Product 1")
                .withSku("SKU-001")
                .build();
        product1.setId(1L);
        
        Product product2 = ProductTestDataBuilder.aProduct()
                .withName("Product 2")
                .withSku("SKU-002")
                .build();
        product2.setId(2L);

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].sku").value("SKU-002"));
    }

    @Test
    void should_ReturnProduct_When_ProductExistsById() throws Exception {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("Test Product")
                .withSku("TEST-SKU")
                .withPrice("29.99")
                .build();
        product.setId(1L);

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.sku").value("TEST-SKU"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    void should_ReturnNotFound_When_ProductDoesNotExistById() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_ReturnProduct_When_ProductExistsBySku() throws Exception {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("SKU Product")
                .withSku("UNIQUE-SKU")
                .withPrice("49.99")
                .build();
        product.setId(5L);

        when(productService.getProductBySku("UNIQUE-SKU")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/sku/UNIQUE-SKU"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("SKU Product"))
                .andExpect(jsonPath("$.sku").value("UNIQUE-SKU"))
                .andExpect(jsonPath("$.price").value(49.99));
    }

    @Test
    void should_ReturnNotFound_When_ProductDoesNotExistBySku() throws Exception {
        when(productService.getProductBySku("NON-EXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_CreateProduct_When_ValidProductProvided() throws Exception {
        Product inputProduct = ProductTestDataBuilder.aProduct()
                .withName("New Product")
                .withSku("NEW-SKU")
                .withPrice("19.99")
                .build();

        Product createdProduct = ProductTestDataBuilder.aProduct()
                .withName("New Product")
                .withSku("NEW-SKU")
                .withPrice("19.99")
                .build();
        createdProduct.setId(10L);

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.sku").value("NEW-SKU"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithDuplicateSku() throws Exception {
        Product inputProduct = ProductTestDataBuilder.aProduct()
                .withSku("DUPLICATE-SKU")
                .build();

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DUPLICATE-SKU already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithInvalidData() throws Exception {
        Product invalidProduct = new Product();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithBlankName() throws Exception {
        Product invalidProduct = ProductTestDataBuilder.aProduct()
                .withName("")
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithBlankSku() throws Exception {
        Product invalidProduct = ProductTestDataBuilder.aProduct()
                .withSku("")
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithNegativePrice() throws Exception {
        Product invalidProduct = ProductTestDataBuilder.aProduct()
                .withPrice("-10.00")
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnBadRequest_When_CreateProductWithZeroPrice() throws Exception {
        Product invalidProduct = ProductTestDataBuilder.aProduct()
                .withPrice("0.00")
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_UpdateProduct_When_ValidProductProvided() throws Exception {
        Product inputProduct = ProductTestDataBuilder.aProduct()
                .withName("Updated Product")
                .withSku("UPDATED-SKU")
                .withPrice("39.99")
                .build();

        Product updatedProduct = ProductTestDataBuilder.aProduct()
                .withName("Updated Product")
                .withSku("UPDATED-SKU")
                .withPrice("39.99")
                .build();
        updatedProduct.setId(1L);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.sku").value("UPDATED-SKU"))
                .andExpect(jsonPath("$.price").value(39.99));
    }

    @Test
    void should_ReturnNotFound_When_UpdateNonExistentProduct() throws Exception {
        Product inputProduct = ProductTestDataBuilder.aProduct().build();

        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_ReturnNotFound_When_UpdateProductWithDuplicateSku() throws Exception {
        Product inputProduct = ProductTestDataBuilder.aProduct()
                .withSku("EXISTING-SKU")
                .build();

        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU EXISTING-SKU already exists"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_ReturnBadRequest_When_UpdateProductWithInvalidData() throws Exception {
        Product invalidProduct = new Product();

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_DeleteProduct_When_ProductExists() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void should_ReturnNotFound_When_DeleteNonExistentProduct() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
