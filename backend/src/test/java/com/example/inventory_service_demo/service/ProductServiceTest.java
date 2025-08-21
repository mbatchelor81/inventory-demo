package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", "TEST123", new BigDecimal("19.99"));
        testProduct.setId(1L);
    }

    @Test
    void createProduct_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        Product newProduct = new Product("New Product", "Description", "NEW123", new BigDecimal("25.99"));
        
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.createProduct(newProduct);
        });
    }

    @Test
    void updateProduct_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        Product updateProduct = new Product("Updated Product", "Updated Description", "UPDATED123", new BigDecimal("35.99"));
        
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.updateProduct(1L, updateProduct);
        });
    }

    @Test
    void deleteProduct_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        doThrow(new DataAccessException("Database connection failed") {}).when(productRepository).delete(any(Product.class));

        assertThrows(DataAccessException.class, () -> {
            productService.deleteProduct(1L);
        });
    }

    @Test
    void getAllProducts_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        when(productRepository.findAll()).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.getAllProducts();
        });
    }

    @Test
    void getProductById_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        when(productRepository.findById(anyLong())).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.getProductById(1L);
        });
    }

    @Test
    void getProductBySku_WhenRepositoryThrowsDataAccessException_ShouldPropagateException() {
        when(productRepository.findBySku(anyString())).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.getProductBySku("TEST123");
        });
    }

    @Test
    void createProduct_WhenExistsBySkuThrowsException_ShouldPropagateException() {
        Product newProduct = new Product("New Product", "Description", "NEW123", new BigDecimal("25.99"));
        
        when(productRepository.existsBySku(anyString())).thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () -> {
            productService.createProduct(newProduct);
        });
    }
}
