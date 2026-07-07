package rs.fon.bg.ac.rs.marinkovicstefan.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Validator validator;
    private Customer mockCustomer;
    private Restaurant mockRestaurant;
    private List<OrderItem> mockOrderItems;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mockCustomer = Mockito.mock(Customer.class);
        mockRestaurant = Mockito.mock(Restaurant.class);
        mockOrderItems = Mockito.mock(List.class);
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    @DisplayName("Should pass validation for every order status with valid fields")
    void validate_ValidOrder_NoViolations(OrderStatus status) {
        Order order = Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .status(status)
                .total(new BigDecimal("1180.00"))
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .orderItems(mockOrderItems)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertTrue(violations.isEmpty(), "Validation should pass for a valid order");
    }

    @Test
    @DisplayName("Should fail validation when order date is null")
    void validate_NullOrderDate_HasViolations() {
        Order order = Order.builder()
                .orderDate(null)
                .status(OrderStatus.PLACED)
                .total(new BigDecimal("1180.00"))
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        assertFalse(violations.isEmpty(), "Validation must fail for null order date");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderDate")),
                "Violation should be linked to 'orderDate' property");
    }

    @Test
    @DisplayName("Should fail validation when status is null")
    void validate_NullStatus_HasViolations() {
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(null)
                .total(new BigDecimal("1180.00"))
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        assertFalse(violations.isEmpty(), "Validation must fail for null status");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")),
                "Violation should be linked to 'status' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1", "-1180.00"})
    @DisplayName("Should fail validation when total is negative")
    void validate_NegativeTotal_HasViolations(String invalidTotal) {
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .total(new BigDecimal(invalidTotal))
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        assertFalse(violations.isEmpty(), "Validation must fail for negative total");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("total")),
                "Violation should be linked to 'total' property");
    }

    @Test
    @DisplayName("Should fail validation when customer is null")
    void validate_NullCustomer_HasViolations() {
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .total(new BigDecimal("1180.00"))
                .customer(null)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        assertFalse(violations.isEmpty(), "Validation must fail for null customer");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("customer")),
                "Violation should be linked to 'customer' property");
    }

    @Test
    @DisplayName("Should fail validation when restaurant is null")
    void validate_NullRestaurant_HasViolations() {
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .total(new BigDecimal("1180.00"))
                .customer(mockCustomer)
                .restaurant(null)
                .build();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        assertFalse(violations.isEmpty(), "Validation must fail for null restaurant");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("restaurant")),
                "Violation should be linked to 'restaurant' property");
    }
}
