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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should pass validation when all customer fields are valid")
    void validate_ValidCustomer_NoViolations() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("Marko Markovic")
                .email("marko.markovic@gmail.com")
                .phone("0641234567")
                .address("Jove Ilica 154, Beograd")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty(), "Validation should pass for a valid customer");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("Should fail validation when customer name is blank")
    void validate_BlankName_HasViolations(String invalidName) {
        Customer customer = Customer.builder()
                .name(invalidName)
                .email("marko.markovic@gmail.com")
                .phone("0641234567")
                .address("Jove Ilica 154, Beograd")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertFalse(violations.isEmpty(), "Validation must fail for blank name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Violation should be linked to 'name' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"marko", "marko@", "@gmail.com", "marko markovic@gmail.com"})
    @DisplayName("Should fail validation when email format is invalid")
    void validate_InvalidEmail_HasViolations(String invalidEmail) {
        Customer customer = Customer.builder()
                .name("Marko Markovic")
                .email(invalidEmail)
                .phone("0641234567")
                .address("Jove Ilica 154, Beograd")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertFalse(violations.isEmpty(), "Validation must fail for invalid email");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                "Violation should be linked to 'email' property");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when phone is blank")
    void validate_BlankPhone_HasViolations(String invalidPhone) {
        Customer customer = Customer.builder()
                .name("Marko Markovic")
                .email("marko.markovic@gmail.com")
                .phone(invalidPhone)
                .address("Jove Ilica 154, Beograd")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertFalse(violations.isEmpty(), "Validation must fail for blank phone");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")),
                "Violation should be linked to 'phone' property");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when address is blank")
    void validate_BlankAddress_HasViolations(String invalidAddress) {
        Customer customer = Customer.builder()
                .name("Marko Markovic")
                .email("marko.markovic@gmail.com")
                .phone("0641234567")
                .address(invalidAddress)
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertFalse(violations.isEmpty(), "Validation must fail for blank address");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("address")),
                "Violation should be linked to 'address' property");
    }
}
