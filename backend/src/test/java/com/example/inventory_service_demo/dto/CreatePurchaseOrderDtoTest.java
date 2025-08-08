package com.example.inventory_service_demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreatePurchaseOrderDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", "john@example.com", Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullCustomerName_ShouldFailValidation() {
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto(null, "john@example.com", Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name is required")));
    }

    @Test
    void shortCustomerName_ShouldFailValidation() {
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("J", "john@example.com", Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name must be between 2 and 100 characters")));
    }

    @Test
    void longCustomerName_ShouldFailValidation() {
        String longName = "J".repeat(101);
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto(longName, "john@example.com", Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name must be between 2 and 100 characters")));
    }

    @Test
    void nullCustomerEmail_ShouldFailValidation() {
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", null, Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer email is required")));
    }

    @Test
    void invalidEmailFormat_ShouldFailValidation() {
        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", "invalid-email", Arrays.asList(itemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void emptyItemsList_ShouldFailValidation() {
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", "john@example.com", Collections.emptyList());

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Order must contain at least one item")));
    }

    @Test
    void nullItemsList_ShouldFailValidation() {
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", "john@example.com", null);

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Order must contain at least one item")));
    }

    @Test
    void invalidOrderItem_ShouldFailValidation() {
        OrderItemDto invalidItemDto = new OrderItemDto(null, 0);
        CreatePurchaseOrderDto dto = new CreatePurchaseOrderDto("John Doe", "john@example.com", Arrays.asList(invalidItemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
