package com.example.inventory_service_demo.util;

import com.example.inventory_service_demo.model.Product;

import java.math.BigDecimal;

public class ProductTestDataBuilder {
    
    private String name = "Test Product";
    private String description = "Test Description";
    private String sku = "TEST-SKU-001";
    private BigDecimal price = new BigDecimal("99.99");
    
    public static ProductTestDataBuilder aProduct() {
        return new ProductTestDataBuilder();
    }
    
    public ProductTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ProductTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ProductTestDataBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }
    
    public ProductTestDataBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    
    public ProductTestDataBuilder withPrice(String price) {
        this.price = new BigDecimal(price);
        return this;
    }
    
    public Product build() {
        return new Product(name, description, sku, price);
    }
}
