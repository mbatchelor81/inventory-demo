package com.example.inventory_service_demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        OrderItemDto dto = new OrderItemDto(1L, 5);

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullProductId_ShouldFailValidation() {
        OrderItemDto dto = new OrderItemDto(null, 5);

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Product ID is required")));
    }

    @Test
    void nullQuantity_ShouldFailValidation() {
        OrderItemDto dto = new OrderItemDto(1L, null);

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity is required")));
    }

    @Test
    void zeroQuantity_ShouldFailValidation() {
        OrderItemDto dto = new OrderItemDto(1L, 0);

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be at least 1")));
    }

    @Test
    void negativeQuantity_ShouldFailValidation() {
        OrderItemDto dto = new OrderItemDto(1L, -1);

        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be at least 1")));
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        OrderItemDto dto = new OrderItemDto();
        
        dto.setProductId(1L);
        dto.setQuantity(5);

        assertEquals(1L, dto.getProductId());
        assertEquals(5, dto.getQuantity());
    }

    @Test
    void constructorWithParameters_ShouldSetFieldsCorrectly() {
        OrderItemDto dto = new OrderItemDto(1L, 5);

        assertEquals(1L, dto.getProductId());
        assertEquals(5, dto.getQuantity());
    }
}
