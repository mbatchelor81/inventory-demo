package com.example.inventory_service_demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating a new purchase order.
 */
public class CreatePurchaseOrderDto {
    
    @NotNull(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @NotNull(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;
    
    // Default constructor
    public CreatePurchaseOrderDto() {
    }
    
    // Constructor with fields
    public CreatePurchaseOrderDto(String customerName, String customerEmail, List<OrderItemDto> items) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
    }
    
    // Getters and Setters
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public List<OrderItemDto> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
