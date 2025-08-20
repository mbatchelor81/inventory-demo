package com.example.inventory_service_demo.util;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.dto.OrderItemDto;
import com.example.inventory_service_demo.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataBuilder {

    public static class ProductBuilder {
        private Long id = 1L;
        private String name = "Test Product";
        private String description = "Test Description";
        private BigDecimal price = BigDecimal.valueOf(10.00);

        public ProductBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            return product;
        }
    }

    public static class InventoryBuilder {
        private Long id = 1L;
        private Product product = new ProductBuilder().build();
        private int quantity = 100;

        public InventoryBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public InventoryBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public InventoryBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Inventory build() {
            Inventory inventory = new Inventory();
            inventory.setId(id);
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            return inventory;
        }
    }

    public static class PurchaseOrderBuilder {
        private Long id = 1L;
        private String customerName = "John Doe";
        private String customerEmail = "john@example.com";
        private OrderStatus status = OrderStatus.CREATED;
        private LocalDateTime orderDate = LocalDateTime.now();
        private List<OrderItem> items = new ArrayList<>();

        public PurchaseOrderBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PurchaseOrderBuilder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public PurchaseOrderBuilder withCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public PurchaseOrderBuilder withStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public PurchaseOrderBuilder withOrderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public PurchaseOrderBuilder withItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public PurchaseOrderBuilder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public PurchaseOrder build() {
            PurchaseOrder order = new PurchaseOrder(customerName, customerEmail);
            order.setId(id);
            order.setStatus(status);
            order.setOrderDate(orderDate);
            
            for (OrderItem item : items) {
                order.addItem(item);
            }
            
            return order;
        }
    }

    public static class OrderItemBuilder {
        private Long id = 1L;
        private Product product = new ProductBuilder().build();
        private int quantity = 5;
        private BigDecimal unitPrice = BigDecimal.valueOf(10.00);

        public OrderItemBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderItemBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public OrderItemBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemBuilder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public OrderItem build() {
            OrderItem item = new OrderItem(product, quantity);
            item.setId(id);
            return item;
        }
    }

    public static class CreatePurchaseOrderDtoBuilder {
        private String customerName = "John Doe";
        private String customerEmail = "john@example.com";
        private List<OrderItemDto> items = Arrays.asList(new OrderItemDtoBuilder().build());

        public CreatePurchaseOrderDtoBuilder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public CreatePurchaseOrderDtoBuilder withCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public CreatePurchaseOrderDtoBuilder withItems(List<OrderItemDto> items) {
            this.items = items;
            return this;
        }

        public CreatePurchaseOrderDto build() {
            CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto();
            dto.setCustomerName(customerName);
            dto.setCustomerEmail(customerEmail);
            dto.setItems(items);
            return dto;
        }
    }

    public static class OrderItemDtoBuilder {
        private Long productId = 1L;
        private int quantity = 5;

        public OrderItemDtoBuilder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public OrderItemDtoBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemDto build() {
            OrderItemDto dto = new OrderItemDto();
            dto.setProductId(productId);
            dto.setQuantity(quantity);
            return dto;
        }
    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public static InventoryBuilder anInventory() {
        return new InventoryBuilder();
    }

    public static PurchaseOrderBuilder aPurchaseOrder() {
        return new PurchaseOrderBuilder();
    }

    public static OrderItemBuilder anOrderItem() {
        return new OrderItemBuilder();
    }

    public static CreatePurchaseOrderDtoBuilder aCreatePurchaseOrderDto() {
        return new CreatePurchaseOrderDtoBuilder();
    }

    public static OrderItemDtoBuilder anOrderItemDto() {
        return new OrderItemDtoBuilder();
    }
}
