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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MenuItemTest {

    private Validator validator;
    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mockRestaurant = mock(Restaurant.class);
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @Test
    @DisplayName("Should pass validation when all menu item fields are valid")
    void validate_ValidMenuItem_NoViolations() {
        MenuItem menuItem = MenuItem.builder()
                .id(1L)
                .name("Cheeseburger")
                .description("Juneca pljeskavica, cedar, kiseli krastavci")
                .price(new BigDecimal("590.00"))
                .available(true)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);
        assertTrue(violations.isEmpty(), "Validation should pass for a valid menu item");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when menu item name is blank")
    void validate_BlankName_HasViolations(String invalidName) {
        MenuItem menuItem = MenuItem.builder()
                .name(invalidName)
                .price(new BigDecimal("590.00"))
                .available(true)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);

        assertFalse(violations.isEmpty(), "Validation must fail for blank name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Violation should be linked to 'name' property");
    }

    @Test
    @DisplayName("Should fail validation when price is null")
    void validate_NullPrice_HasViolations() {
        MenuItem menuItem = MenuItem.builder()
                .name("Cheeseburger")
                .price(null)
                .available(true)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);

        assertFalse(violations.isEmpty(), "Validation must fail for null price");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")),
                "Violation should be linked to 'price' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-590.00"})
    @DisplayName("Should fail validation when price is zero or negative")
    void validate_NonPositivePrice_HasViolations(String invalidPrice) {
        MenuItem menuItem = MenuItem.builder()
                .name("Cheeseburger")
                .price(new BigDecimal(invalidPrice))
                .available(true)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);

        assertFalse(violations.isEmpty(), "Validation must fail for non-positive price");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")),
                "Violation should be linked to 'price' property");
    }

    @Test
    @DisplayName("Should fail validation when restaurant is null")
    void validate_NullRestaurant_HasViolations() {
        MenuItem menuItem = MenuItem.builder()
                .name("Cheeseburger")
                .price(new BigDecimal("590.00"))
                .available(true)
                .restaurant(null)
                .build();

        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);

        assertFalse(violations.isEmpty(), "Validation must fail for null restaurant");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("restaurant")),
                "Violation should be linked to 'restaurant' property");
    }
}
