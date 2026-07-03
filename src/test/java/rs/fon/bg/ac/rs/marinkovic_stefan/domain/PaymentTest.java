package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

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
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Validator validator;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mockOrder = Mockito.mock(Order.class);
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @EnumSource(PayMethod.class)
    @DisplayName("Should pass validation for every payment method with valid fields")
    void validate_ValidPayment_NoViolations(PayMethod method) {
        Payment payment = Payment.builder()
                .id(1L)
                .amount(new BigDecimal("1180.00"))
                .method(method)
                .paidAt(LocalDateTime.now())
                .order(mockOrder)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertTrue(violations.isEmpty(), "Validation should pass for a valid payment");
    }

    @Test
    @DisplayName("Should fail validation when amount is null")
    void validate_NullAmount_HasViolations() {
        Payment payment = Payment.builder()
                .amount(null)
                .method(PayMethod.CARD)
                .paidAt(LocalDateTime.now())
                .order(mockOrder)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        assertFalse(violations.isEmpty(), "Validation must fail for null amount");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")),
                "Violation should be linked to 'amount' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-1180.00"})
    @DisplayName("Should fail validation when amount is zero or negative")
    void validate_NonPositiveAmount_HasViolations(String invalidAmount) {
        Payment payment = Payment.builder()
                .amount(new BigDecimal(invalidAmount))
                .method(PayMethod.CASH)
                .paidAt(LocalDateTime.now())
                .order(mockOrder)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        assertFalse(violations.isEmpty(), "Validation must fail for non-positive amount");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")),
                "Violation should be linked to 'amount' property");
    }

    @Test
    @DisplayName("Should fail validation when payment method is null")
    void validate_NullMethod_HasViolations() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal("1180.00"))
                .method(null)
                .paidAt(LocalDateTime.now())
                .order(mockOrder)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        assertFalse(violations.isEmpty(), "Validation must fail for null method");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("method")),
                "Violation should be linked to 'method' property");
    }

    @Test
    @DisplayName("Should fail validation when payment date is null")
    void validate_NullPaidAt_HasViolations() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal("1180.00"))
                .method(PayMethod.PAYPAL)
                .paidAt(null)
                .order(mockOrder)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        assertFalse(violations.isEmpty(), "Validation must fail for null payment date");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paidAt")),
                "Violation should be linked to 'paidAt' property");
    }

    @Test
    @DisplayName("Should fail validation when order is null")
    void validate_NullOrder_HasViolations() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal("1180.00"))
                .method(PayMethod.CARD)
                .paidAt(LocalDateTime.now())
                .order(null)
                .build();

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        assertFalse(violations.isEmpty(), "Validation must fail for null order");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("order")),
                "Violation should be linked to 'order' property");
    }
}
