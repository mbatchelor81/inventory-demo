package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product createSampleProduct() {
        return new Product("Test Product", "Description", "SKU123", new BigDecimal("99.99"));
    }

    private Product createSampleProductWithId(Long id) {
        Product product = createSampleProduct();
        product.setId(id);
        return product;
    }

    @Test
    void testGetAllProducts_ReturnsListOfProducts() {
        Product product1 = createSampleProductWithId(1L);
        Product product2 = createSampleProductWithId(2L);
        product2.setName("Second Product");
        product2.setSku("SKU456");
        Product product3 = createSampleProductWithId(3L);
        product3.setName("Third Product");
        product3.setSku("SKU789");
        
        List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> result = productService.getAllProducts();

        assertEquals(expectedProducts, result);
        assertEquals(3, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_ExistingProduct_ReturnsProduct() {
        Product product = createSampleProductWithId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NonExistingProduct_ReturnsEmpty() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateProduct_ValidProduct_Success() {
        Product product = createSampleProduct();
        Product savedProduct = createSampleProductWithId(1L);
        
        when(productRepository.existsBySku("SKU123")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("SKU123", result.getSku());
        verify(productRepository, times(1)).existsBySku("SKU123");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCreateProduct_DuplicateSku_ThrowsException() {
        Product product = createSampleProduct();
        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(product)
        );

        assertEquals("Product with SKU SKU123 already exists", exception.getMessage());
        verify(productRepository, times(1)).existsBySku("SKU123");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ValidData_Success() {
        Product existingProduct = createSampleProductWithId(1L);
        Product updateProduct = new Product("Updated Product", "Updated Description", "SKU123", new BigDecimal("149.99"));
        Product savedProduct = createSampleProductWithId(1L);
        savedProduct.setName("Updated Product");
        savedProduct.setDescription("Updated Description");
        savedProduct.setPrice(new BigDecimal("149.99"));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.updateProduct(1L, updateProduct);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("SKU123", result.getSku());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(existingProduct);
        verify(productRepository, never()).existsBySku(any());
    }

    @Test
    void testUpdateProduct_NonExistingProduct_ThrowsException() {
        Product updateProduct = createSampleProduct();
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.updateProduct(999L, updateProduct)
        );

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_DuplicateSkuOnChange_ThrowsException() {
        Product existingProduct = createSampleProductWithId(1L);
        existingProduct.setSku("OLD_SKU");
        Product updateProduct = new Product("Updated Product", "Updated Description", "NEW_SKU", new BigDecimal("149.99"));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("NEW_SKU")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.updateProduct(1L, updateProduct)
        );

        assertEquals("Product with SKU NEW_SKU already exists", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsBySku("NEW_SKU");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_SkuChangeToAvailableSku_Success() {
        Product existingProduct = createSampleProductWithId(1L);
        existingProduct.setSku("OLD_SKU");
        Product updateProduct = new Product("Updated Product", "Updated Description", "NEW_SKU", new BigDecimal("149.99"));
        Product savedProduct = createSampleProductWithId(1L);
        savedProduct.setName("Updated Product");
        savedProduct.setDescription("Updated Description");
        savedProduct.setSku("NEW_SKU");
        savedProduct.setPrice(new BigDecimal("149.99"));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("NEW_SKU")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.updateProduct(1L, updateProduct);

        assertNotNull(result);
        assertEquals("NEW_SKU", result.getSku());
        assertEquals("Updated Product", result.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsBySku("NEW_SKU");
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testDeleteProduct_ExistingProduct_Success() {
        Product product = createSampleProductWithId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NonExistingProduct_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.deleteProduct(999L)
        );

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void testGetProductBySku_ExistingProduct_ReturnsProduct() {
        Product product = createSampleProductWithId(1L);
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductBySku("SKU123");

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        assertEquals("SKU123", result.get().getSku());
        verify(productRepository, times(1)).findBySku("SKU123");
    }

    @Test
    void testGetProductBySku_NonExistingProduct_ReturnsEmpty() {
        when(productRepository.findBySku("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductBySku("NONEXISTENT");

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findBySku("NONEXISTENT");
    }
}
