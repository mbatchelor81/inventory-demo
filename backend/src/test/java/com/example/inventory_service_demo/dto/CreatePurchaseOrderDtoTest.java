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
    private CreatePurchaseOrderDto orderDto;
    private OrderItemDto validItemDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validItemDto = new OrderItemDto();
        validItemDto.setProductId(1L);
        validItemDto.setQuantity(5);

        orderDto = new CreatePurchaseOrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setCustomerEmail("john@example.com");
        orderDto.setItems(Arrays.asList(validItemDto));
    }

    @Test
    void should_PassValidation_When_AllFieldsAreValid() {
        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void should_FailValidation_When_CustomerNameIsNull() {
        orderDto.setCustomerName(null);

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name is required")));
    }

    @Test
    void should_FailValidation_When_CustomerNameIsTooShort() {
        orderDto.setCustomerName("A");

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name must be between 2 and 100 characters")));
    }

    @Test
    void should_FailValidation_When_CustomerNameIsTooLong() {
        orderDto.setCustomerName("A".repeat(101));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer name must be between 2 and 100 characters")));
    }

    @Test
    void should_FailValidation_When_CustomerEmailIsNull() {
        orderDto.setCustomerEmail(null);

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer email is required")));
    }

    @Test
    void should_FailValidation_When_CustomerEmailIsInvalid() {
        orderDto.setCustomerEmail("invalid-email");

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void should_PassValidation_When_CustomerEmailIsValid() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@example.com"
        };

        for (String email : validEmails) {
            orderDto.setCustomerEmail(email);
            Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);
            
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void should_FailValidation_When_ItemsListIsEmpty() {
        orderDto.setItems(Collections.emptyList());

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Order must contain at least one item")));
    }

    @Test
    void should_FailValidation_When_ItemsListIsNull() {
        orderDto.setItems(null);

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Order must contain at least one item")));
    }

    @Test
    void should_ValidateNestedItems_When_ItemsContainInvalidData() {
        OrderItemDto invalidItemDto = new OrderItemDto();
        invalidItemDto.setProductId(null);
        invalidItemDto.setQuantity(-1);

        orderDto.setItems(Arrays.asList(invalidItemDto));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void should_PassValidation_When_MultipleValidItemsProvided() {
        OrderItemDto item1 = new OrderItemDto();
        item1.setProductId(1L);
        item1.setQuantity(5);

        OrderItemDto item2 = new OrderItemDto();
        item2.setProductId(2L);
        item2.setQuantity(3);

        orderDto.setItems(Arrays.asList(item1, item2));

        Set<ConstraintViolation<CreatePurchaseOrderDto>> violations = validator.validate(orderDto);

        assertTrue(violations.isEmpty());
    }
}
