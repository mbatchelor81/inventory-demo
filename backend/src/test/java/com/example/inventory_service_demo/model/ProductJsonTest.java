package com.example.inventory_service_demo.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ProductJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeProductToJson() throws Exception {
        Product product = new Product("Test Product", "Test Description", "TEST001", new BigDecimal("19.99"));
        product.setId(1L);

        String json = objectMapper.writeValueAsString(product);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Product\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"sku\":\"TEST001\"");
        assertThat(json).contains("\"price\":19.99");
    }

    @Test
    void shouldDeserializeJsonToProduct() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Product\",\"description\":\"Test Description\",\"sku\":\"TEST001\",\"price\":19.99}";

        Product product = objectMapper.readValue(json, Product.class);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("Test Description");
        assertThat(product.getSku()).isEqualTo("TEST001");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("19.99"));
    }

    @Test
    void shouldSerializeProductWithNullDescription() throws Exception {
        Product product = new Product("Test Product", null, "TEST001", new BigDecimal("19.99"));
        product.setId(1L);

        String json = objectMapper.writeValueAsString(product);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Product\"");
        assertThat(json).contains("\"sku\":\"TEST001\"");
        assertThat(json).contains("\"price\":19.99");
        assertThat(json).contains("\"description\":null");
    }

    @Test
    void shouldDeserializeJsonWithNullDescription() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Product\",\"sku\":\"TEST001\",\"price\":19.99}";

        Product product = objectMapper.readValue(json, Product.class);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isNull();
        assertThat(product.getSku()).isEqualTo("TEST001");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("19.99"));
    }

    @Test
    void shouldHandleBigDecimalPrecisionInSerialization() throws Exception {
        Product product = new Product("Precision Product", "Test precision", "PREC001", new BigDecimal("123.456789"));
        product.setId(1L);

        String json = objectMapper.writeValueAsString(product);

        assertThat(json).contains("\"price\":123.456789");
    }

    @Test
    void shouldHandleBigDecimalPrecisionInDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"Precision Product\",\"description\":\"Test precision\",\"sku\":\"PREC001\",\"price\":123.456789}";

        Product product = objectMapper.readValue(json, Product.class);

        assertThat(product.getPrice()).isEqualTo(new BigDecimal("123.456789"));
    }

    @Test
    void shouldSerializeProductWithoutId() throws Exception {
        Product product = new Product("New Product", "New Description", "NEW001", new BigDecimal("25.50"));

        String json = objectMapper.writeValueAsString(product);

        assertThat(json).contains("\"name\":\"New Product\"");
        assertThat(json).contains("\"description\":\"New Description\"");
        assertThat(json).contains("\"sku\":\"NEW001\"");
        assertThat(json).contains("\"price\":25.50");
    }

    @Test
    void shouldDeserializeJsonWithoutId() throws Exception {
        String json = "{\"name\":\"New Product\",\"description\":\"New Description\",\"sku\":\"NEW001\",\"price\":25.50}";

        Product product = objectMapper.readValue(json, Product.class);

        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("New Product");
        assertThat(product.getDescription()).isEqualTo("New Description");
        assertThat(product.getSku()).isEqualTo("NEW001");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("25.50"));
    }
}
