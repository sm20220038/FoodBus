package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvSource({
            "Burger House, Roostilj, 0.0",
            "Pizza Bar, Italijanska, 4.5",
            "Wok In, Kineska, 5.0"
    })
    @DisplayName("Should pass validation with valid restaurant parameters")
    void validate_ValidRestaurant_NoViolations(String name, String cuisine, double rating) {
        Restaurant restaurant = Restaurant.builder()
                .id(UUID.randomUUID())
                .name(name)
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine(cuisine)
                .rating(rating)
                .email("kontakt@restoran.rs")
                .build();

        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);
        assertTrue(violations.isEmpty(), "Validation should pass for valid restaurants");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when restaurant name is blank")
    void validate_BlankName_HasViolations(String invalidName) {
        Restaurant restaurant = Restaurant.builder()
                .name(invalidName)
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(4.0)
                .email("kontakt@restoran.rs")
                .build();

        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);

        assertFalse(violations.isEmpty(), "Validation must fail for blank name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Violation should be linked to 'name' property");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.5, -1.0, 5.1, 6.0, 100.0})
    @DisplayName("Should fail validation when rating is outside the 0 to 5 range")
    void validate_RatingOutOfRange_HasViolations(double invalidRating) {
        Restaurant restaurant = Restaurant.builder()
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(invalidRating)
                .email("kontakt@restoran.rs")
                .build();

        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);

        assertFalse(violations.isEmpty(), "Validation must fail for rating out of range");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rating")),
                "Violation should be linked to 'rating' property");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when cuisine is blank")
    void validate_BlankCuisine_HasViolations(String invalidCuisine) {
        Restaurant restaurant = Restaurant.builder()
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine(invalidCuisine)
                .rating(4.0)
                .email("kontakt@restoran.rs")
                .build();

        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);

        assertFalse(violations.isEmpty(), "Validation must fail for blank cuisine");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cuisine")),
                "Violation should be linked to 'cuisine' property");
    }

    @ParameterizedTest
    @ValueSource(strings = {"kontakt", "kontakt@", "@restoran.rs"})
    @DisplayName("Should fail validation when restaurant email is invalid")
    void validate_InvalidEmail_HasViolations(String invalidEmail) {
        Restaurant restaurant = Restaurant.builder()
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(4.0)
                .email(invalidEmail)
                .build();

        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);

        assertFalse(violations.isEmpty(), "Validation must fail for invalid email");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                "Violation should be linked to 'email' property");
    }
}
