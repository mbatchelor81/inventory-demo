package com.example.inventory_service_demo;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import com.example.inventory_service_demo.repository.InventoryRepository;
import com.example.inventory_service_demo.repository.OrderItemRepository;
import com.example.inventory_service_demo.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    private Product createValidProduct() {
        return new Product("Test Product", "Test Description", "TEST-SKU-001", new BigDecimal("99.99"));
    }

    private Product createValidProductWithSku(String sku) {
        return new Product("Test Product", "Test Description", sku, new BigDecimal("99.99"));
    }

    @Test
    void testGetAllProducts_ReturnsEmptyList_WhenNoProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllProducts_ReturnsProductList_WhenProductsExist() throws Exception {
        Product product = createValidProduct();
        productRepository.save(product);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$[0].price", is(99.99)));
    }

    @Test
    void testGetProductById_ReturnsProduct_WhenProductExists() throws Exception {
        Product product = createValidProduct();
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void testGetProductById_Returns404_WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProductBySku_ReturnsProduct_WhenProductExists() throws Exception {
        Product product = createValidProduct();
        productRepository.save(product);

        mockMvc.perform(get("/api/products/sku/{sku}", "TEST-SKU-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void testGetProductBySku_Returns404_WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/sku/{sku}", "NONEXISTENT-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct_ReturnsCreated_WithValidProduct() throws Exception {
        Product product = createValidProduct();
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-001")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testCreateProduct_Returns400_WithDuplicateSku() throws Exception {
        Product existingProduct = createValidProduct();
        productRepository.save(existingProduct);

        Product duplicateProduct = createValidProduct();
        String productJson = objectMapper.writeValueAsString(duplicateProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithMissingName() throws Exception {
        Product product = createValidProduct();
        product.setName(null);
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithBlankName() throws Exception {
        Product product = createValidProduct();
        product.setName("");
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithMissingSku() throws Exception {
        Product product = createValidProduct();
        product.setSku(null);
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithBlankSku() throws Exception {
        Product product = createValidProduct();
        product.setSku("");
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithNullPrice() throws Exception {
        Product product = createValidProduct();
        product.setPrice(null);
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithNegativePrice() throws Exception {
        Product product = createValidProduct();
        product.setPrice(new BigDecimal("-10.00"));
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithZeroPrice() throws Exception {
        Product product = createValidProduct();
        product.setPrice(BigDecimal.ZERO);
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_Returns400_WithMalformedJson() throws Exception {
        String malformedJson = "{\"name\":\"Test\",\"sku\":\"TEST\",\"price\":invalid}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProduct_ReturnsUpdatedProduct_WhenProductExists() throws Exception {
        Product existingProduct = createValidProduct();
        Product savedProduct = productRepository.save(existingProduct);

        Product updatedProduct = createValidProductWithSku("UPDATED-SKU");
        updatedProduct.setName("Updated Product");
        String productJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.sku", is("UPDATED-SKU")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void testUpdateProduct_Returns404_WhenProductNotFound() throws Exception {
        Product product = createValidProduct();
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(put("/api/products/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_Returns400_WithDuplicateSku() throws Exception {
        Product product1 = createValidProductWithSku("SKU-001");
        Product product2 = createValidProductWithSku("SKU-002");
        Product savedProduct1 = productRepository.save(product1);
        Product savedProduct2 = productRepository.save(product2);

        Product updateRequest = createValidProductWithSku("SKU-001");
        updateRequest.setName("Updated Product");
        String productJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/products/{id}", savedProduct2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_Returns400_WithValidationErrors() throws Exception {
        Product existingProduct = createValidProduct();
        Product savedProduct = productRepository.save(existingProduct);

        Product invalidProduct = createValidProduct();
        invalidProduct.setName("");
        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteProduct_ReturnsNoContent_WhenProductExists() throws Exception {
        Product product = createValidProduct();
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProduct_Returns404_WhenProductNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

}
