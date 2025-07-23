package com.example.inventory_service_demo.model;

import com.example.inventory_service_demo.util.ProductTestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_PassValidation_When_AllFieldsAreValid() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("Valid Product")
                .withDescription("Valid Description")
                .withSku("VALID-SKU")
                .withPrice("29.99")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_FailValidation_When_NameIsNull() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName(null)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product name is required");
    }

    @Test
    void should_FailValidation_When_NameIsEmpty() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product name is required");
    }

    @Test
    void should_FailValidation_When_NameIsBlank() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("   ")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product name is required");
    }

    @Test
    void should_FailValidation_When_SkuIsNull() {
        Product product = ProductTestDataBuilder.aProduct()
                .withSku(null)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("SKU is required");
    }

    @Test
    void should_FailValidation_When_SkuIsEmpty() {
        Product product = ProductTestDataBuilder.aProduct()
                .withSku("")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("SKU is required");
    }

    @Test
    void should_FailValidation_When_SkuIsBlank() {
        Product product = ProductTestDataBuilder.aProduct()
                .withSku("   ")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("SKU is required");
    }

    @Test
    void should_FailValidation_When_PriceIsNull() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice((BigDecimal) null)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price is required");
    }

    @Test
    void should_FailValidation_When_PriceIsNegative() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice("-10.00")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price must be positive");
    }

    @Test
    void should_FailValidation_When_PriceIsZero() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice("0.00")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price must be positive");
    }

    @Test
    void should_PassValidation_When_DescriptionIsNull() {
        Product product = ProductTestDataBuilder.aProduct()
                .withDescription(null)
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_PassValidation_When_DescriptionIsEmpty() {
        Product product = ProductTestDataBuilder.aProduct()
                .withDescription("")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_FailValidation_When_MultipleFieldsAreInvalid() {
        Product product = ProductTestDataBuilder.aProduct()
                .withName("")
                .withSku("")
                .withPrice("-5.00")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Product name is required",
                        "SKU is required",
                        "Price must be positive"
                );
    }

    @Test
    void should_PassValidation_When_PriceHasValidDecimalPlaces() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice("99.99")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_PassValidation_When_PriceIsVerySmall() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice("0.01")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_PassValidation_When_PriceIsVeryLarge() {
        Product product = ProductTestDataBuilder.aProduct()
                .withPrice("999999.99")
                .build();

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
    }
}
