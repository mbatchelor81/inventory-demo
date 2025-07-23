package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import com.example.inventory_service_demo.util.ProductTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = ProductTestDataBuilder.aProduct()
                .withName("Test Product")
                .withSku("TEST-SKU")
                .withPrice("29.99")
                .build();
        testProduct.setId(1L);
    }

    @Test
    void should_ReturnAllProducts_When_ProductsExist() {
        Product product1 = ProductTestDataBuilder.aProduct()
                .withName("Product 1")
                .withSku("SKU-001")
                .build();
        Product product2 = ProductTestDataBuilder.aProduct()
                .withName("Product 2")
                .withSku("SKU-002")
                .build();
        List<Product> expectedProducts = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.getAllProducts();

        assertThat(actualProducts).hasSize(2);
        assertThat(actualProducts).containsExactlyElementsOf(expectedProducts);
    }

    @Test
    void should_ReturnEmptyList_When_NoProductsExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> actualProducts = productService.getAllProducts();

        assertThat(actualProducts).isEmpty();
    }

    @Test
    void should_ReturnProduct_When_ProductExistsById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProduct);
    }

    @Test
    void should_ReturnEmpty_When_ProductDoesNotExistById() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void should_ReturnProduct_When_ProductExistsBySku() {
        when(productRepository.findBySku("TEST-SKU")).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductBySku("TEST-SKU");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProduct);
    }

    @Test
    void should_ReturnEmpty_When_ProductDoesNotExistBySku() {
        when(productRepository.findBySku("NON-EXISTENT")).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductBySku("NON-EXISTENT");

        assertThat(result).isEmpty();
    }

    @Test
    void should_CreateProduct_When_SkuIsUnique() {
        Product newProduct = ProductTestDataBuilder.aProduct()
                .withSku("UNIQUE-SKU")
                .build();

        when(productRepository.existsBySku("UNIQUE-SKU")).thenReturn(false);
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        Product result = productService.createProduct(newProduct);

        assertThat(result).isEqualTo(newProduct);
        verify(productRepository).existsBySku("UNIQUE-SKU");
        verify(productRepository).save(newProduct);
    }

    @Test
    void should_ThrowException_When_CreateProductWithDuplicateSku() {
        Product newProduct = ProductTestDataBuilder.aProduct()
                .withSku("DUPLICATE-SKU")
                .build();

        when(productRepository.existsBySku("DUPLICATE-SKU")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(newProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with SKU DUPLICATE-SKU already exists");

        verify(productRepository).existsBySku("DUPLICATE-SKU");
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_UpdateProduct_When_ProductExistsAndSkuIsUnique() {
        Product existingProduct = ProductTestDataBuilder.aProduct()
                .withName("Old Name")
                .withSku("OLD-SKU")
                .withPrice("19.99")
                .build();
        existingProduct.setId(1L);

        Product updateDetails = ProductTestDataBuilder.aProduct()
                .withName("New Name")
                .withSku("NEW-SKU")
                .withPrice("29.99")
                .build();

        Product updatedProduct = ProductTestDataBuilder.aProduct()
                .withName("New Name")
                .withSku("NEW-SKU")
                .withPrice("29.99")
                .build();
        updatedProduct.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("NEW-SKU")).thenReturn(false);
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updateDetails);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getSku()).isEqualTo("NEW-SKU");
        assertThat(result.getPrice()).isEqualTo(updateDetails.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).existsBySku("NEW-SKU");
        verify(productRepository).save(existingProduct);
    }

    @Test
    void should_UpdateProduct_When_SkuRemainsTheSame() {
        Product existingProduct = ProductTestDataBuilder.aProduct()
                .withName("Old Name")
                .withSku("SAME-SKU")
                .withPrice("19.99")
                .build();
        existingProduct.setId(1L);

        Product updateDetails = ProductTestDataBuilder.aProduct()
                .withName("New Name")
                .withSku("SAME-SKU")
                .withPrice("29.99")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product result = productService.updateProduct(1L, updateDetails);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getSku()).isEqualTo("SAME-SKU");
        verify(productRepository).findById(1L);
        verify(productRepository, never()).existsBySku(any());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void should_ThrowException_When_UpdateNonExistentProduct() {
        Product updateDetails = ProductTestDataBuilder.aProduct().build();

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_ThrowException_When_UpdateProductWithDuplicateSku() {
        Product existingProduct = ProductTestDataBuilder.aProduct()
                .withSku("OLD-SKU")
                .build();
        existingProduct.setId(1L);

        Product updateDetails = ProductTestDataBuilder.aProduct()
                .withSku("EXISTING-SKU")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("EXISTING-SKU")).thenReturn(true);

        assertThatThrownBy(() -> productService.updateProduct(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with SKU EXISTING-SKU already exists");

        verify(productRepository).findById(1L);
        verify(productRepository).existsBySku("EXISTING-SKU");
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_DeleteProduct_When_ProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }

    @Test
    void should_ThrowException_When_DeleteNonExistentProduct() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).delete(any());
    }
}
