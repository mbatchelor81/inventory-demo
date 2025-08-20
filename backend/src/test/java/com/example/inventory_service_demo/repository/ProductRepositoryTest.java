package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.util.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void should_FindProduct_When_ProductExistsBySku() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("Test Product")
                .withSku("FIND-BY-SKU")
                .withPrice("25.99")
                .build();

        entityManager.persistAndFlush(product);

        Optional<Product> result = productRepository.findBySku("FIND-BY-SKU");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Product");
        assertThat(result.get().getSku()).isEqualTo("FIND-BY-SKU");
        assertThat(result.get().getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void should_ReturnEmpty_When_ProductDoesNotExistBySku() {
        Optional<Product> result = productRepository.findBySku("NON-EXISTENT-SKU");

        assertThat(result).isEmpty();
    }

    @Test
    void should_ReturnTrue_When_ProductExistsBySku() {
        Product product = ProductTestDataBuilder.aProduct()
                .withSku("EXISTS-SKU")
                .build();

        entityManager.persistAndFlush(product);

        boolean exists = productRepository.existsBySku("EXISTS-SKU");

        assertThat(exists).isTrue();
    }

    @Test
    void should_ReturnFalse_When_ProductDoesNotExistBySku() {
        boolean exists = productRepository.existsBySku("DOES-NOT-EXIST");

        assertThat(exists).isFalse();
    }

    @Test
    void should_SaveAndRetrieveProduct_When_ValidProductProvided() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("Repository Test Product")
                .withDescription("Test Description")
                .withSku("REPO-TEST-SKU")
                .withPrice("15.50")
                .build();

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Repository Test Product");
        assertThat(savedProduct.getDescription()).isEqualTo("Test Description");
        assertThat(savedProduct.getSku()).isEqualTo("REPO-TEST-SKU");
        assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());

        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertThat(retrievedProduct).isPresent();
        assertThat(retrievedProduct.get()).isEqualTo(savedProduct);
    }

    @Test
    void should_UpdateProduct_When_ProductExists() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("Original Name")
                .withSku("UPDATE-SKU")
                .withPrice("10.00")
                .build();

        Product savedProduct = entityManager.persistAndFlush(product);
        Long productId = savedProduct.getId();

        savedProduct.setName("Updated Name");
        savedProduct.setPrice(ProductTestDataBuilder.aProduct().withPrice("20.00").build().getPrice());
        
        Product updatedProduct = productRepository.save(savedProduct);

        assertThat(updatedProduct.getId()).isEqualTo(productId);
        assertThat(updatedProduct.getName()).isEqualTo("Updated Name");
        assertThat(updatedProduct.getSku()).isEqualTo("UPDATE-SKU");
        assertThat(updatedProduct.getPrice()).isEqualTo(ProductTestDataBuilder.aProduct().withPrice("20.00").build().getPrice());
    }

    @Test
    void should_DeleteProduct_When_ProductExists() {
        Product product = ProductTestDataBuilder.aProduct()
                .withSku("DELETE-SKU")
                .build();

        Product savedProduct = entityManager.persistAndFlush(product);
        Long productId = savedProduct.getId();

        productRepository.delete(savedProduct);

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    void should_FindAllProducts_When_MultipleProductsExist() {
        Product product1 = ProductTestDataBuilder.aProduct()
                .withName("Product 1")
                .withSku("SKU-1")
                .build();
        Product product2 = ProductTestDataBuilder.aProduct()
                .withName("Product 2")
                .withSku("SKU-2")
                .build();

        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);

        Iterable<Product> allProducts = productRepository.findAll();

        assertThat(allProducts).hasSize(2);
        assertThat(allProducts).extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
    }
}
