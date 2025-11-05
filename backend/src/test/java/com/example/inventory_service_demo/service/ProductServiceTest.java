package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSearchProductsReturnsMatchingProducts() {
        Product product1 = new Product();
        product1.setName("Test Laptop");
        product1.setSku("TEST-LAPTOP-001");
        product1.setPrice(new BigDecimal("999.99"));
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Test Mouse");
        product2.setSku("TEST-MOUSE-001");
        product2.setPrice(new BigDecimal("29.99"));
        productRepository.save(product2);

        List<Product> results = productService.searchProducts("Laptop");
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Laptop")));
    }

    @Test
    void testGenerateProductCodeReturnsFormattedCode() {
        String code = productService.generateProductCode();
        
        assertNotNull(code);
        assertTrue(code.startsWith("PROD-"));
        assertEquals(11, code.length());
    }

    @Test
    void testGenerateProductHashReturnsValidMD5Hash() {
        String input = "test-product-123";
        String hash = productService.generateProductHash(input);
        
        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertTrue(hash.matches("[0-9a-f]{32}"));
    }

}
