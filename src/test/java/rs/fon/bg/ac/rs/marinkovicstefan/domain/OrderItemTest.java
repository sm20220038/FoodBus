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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private Validator validator;
    private Order mockOrder;
    private MenuItem mockMenuItem;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mockOrder = Mockito.mock(Order.class);
        mockMenuItem = Mockito.mock(MenuItem.class);
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @ParameterizedTest
    @CsvSource({
            "1, 590.00, 590.00",
            "2, 590.00, 1180.00",
            "10, 120.50, 1205.00"
    })
    @DisplayName("Should pass validation with valid order item parameters")
    void validate_ValidOrderItem_NoViolations(int quantity, String unitPrice, String subtotal) {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .quantity(quantity)
                .unitPrice(new BigDecimal(unitPrice))
                .subtotal(new BigDecimal(subtotal))
                .order(mockOrder)
                .menuItem(mockMenuItem)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertTrue(violations.isEmpty(), "Validation should pass for valid order items");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Should fail validation when quantity is lower than 1")
    void validate_InvalidQuantity_HasViolations(int invalidQuantity) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(invalidQuantity)
                .unitPrice(new BigDecimal("590.00"))
                .subtotal(new BigDecimal("590.00"))
                .order(mockOrder)
                .menuItem(mockMenuItem)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertFalse(violations.isEmpty(), "Validation must fail for quantity lower than 1");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")),
                "Violation should be linked to 'quantity' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-590.00"})
    @DisplayName("Should fail validation when unit price is zero or negative")
    void validate_NonPositiveUnitPrice_HasViolations(String invalidUnitPrice) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .unitPrice(new BigDecimal(invalidUnitPrice))
                .subtotal(new BigDecimal("590.00"))
                .order(mockOrder)
                .menuItem(mockMenuItem)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertFalse(violations.isEmpty(), "Validation must fail for non-positive unit price");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("unitPrice")),
                "Violation should be linked to 'unitPrice' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-590.00"})
    @DisplayName("Should fail validation when subtotal is negative")
    void validate_NegativeSubtotal_HasViolations(String invalidSubtotal) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .unitPrice(new BigDecimal("590.00"))
                .subtotal(new BigDecimal(invalidSubtotal))
                .order(mockOrder)
                .menuItem(mockMenuItem)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertFalse(violations.isEmpty(), "Validation must fail for negative subtotal");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subtotal")),
                "Violation should be linked to 'subtotal' property");
    }

    @Test
    @DisplayName("Should fail validation when order is null")
    void validate_NullOrder_HasViolations() {
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .unitPrice(new BigDecimal("590.00"))
                .subtotal(new BigDecimal("590.00"))
                .order(null)
                .menuItem(mockMenuItem)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertFalse(violations.isEmpty(), "Validation must fail for null order");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("order")),
                "Violation should be linked to 'order' property");
    }

    @Test
    @DisplayName("Should fail validation when menu item is null")
    void validate_NullMenuItem_HasViolations() {
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .unitPrice(new BigDecimal("590.00"))
                .subtotal(new BigDecimal("590.00"))
                .order(mockOrder)
                .menuItem(null)
                .build();

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertFalse(violations.isEmpty(), "Validation must fail for null menu item");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("menuItem")),
                "Violation should be linked to 'menuItem' property");
    }
}
