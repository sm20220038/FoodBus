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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private Validator validator;
    private Customer mockCustomer;
    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mockCustomer = Mockito.mock(Customer.class);
        mockRestaurant = Mockito.mock(Restaurant.class);
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Should pass validation for every rating between 1 and 5")
    void validate_ValidReview_NoViolations(int rating) {
        Review review = Review.builder()
                .id(1L)
                .rating(rating)
                .comment("Odlicna hrana, brza dostava!")
                .createdAt(LocalDateTime.now())
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertTrue(violations.isEmpty(), "Validation should pass for valid reviews");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 10, 100})
    @DisplayName("Should fail validation when rating is outside the 1 to 5 range")
    void validate_RatingOutOfRange_HasViolations(int invalidRating) {
        Review review = Review.builder()
                .rating(invalidRating)
                .comment("Odlicna hrana, brza dostava!")
                .createdAt(LocalDateTime.now())
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(violations.isEmpty(), "Validation must fail for rating out of range");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("rating")),
                "Violation should be linked to 'rating' property");
    }

    @Test
    @DisplayName("Should fail validation when creation date is null")
    void validate_NullCreatedAt_HasViolations() {
        Review review = Review.builder()
                .rating(5)
                .comment("Odlicna hrana, brza dostava!")
                .createdAt(null)
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(violations.isEmpty(), "Validation must fail for null creation date");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("createdAt")),
                "Violation should be linked to 'createdAt' property");
    }

    @Test
    @DisplayName("Should fail validation when customer is null")
    void validate_NullCustomer_HasViolations() {
        Review review = Review.builder()
                .rating(5)
                .comment("Odlicna hrana, brza dostava!")
                .createdAt(LocalDateTime.now())
                .customer(null)
                .restaurant(mockRestaurant)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(violations.isEmpty(), "Validation must fail for null customer");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("customer")),
                "Violation should be linked to 'customer' property");
    }

    @Test
    @DisplayName("Should fail validation when restaurant is null")
    void validate_NullRestaurant_HasViolations() {
        Review review = Review.builder()
                .rating(5)
                .comment("Odlicna hrana, brza dostava!")
                .createdAt(LocalDateTime.now())
                .customer(mockCustomer)
                .restaurant(null)
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        assertFalse(violations.isEmpty(), "Validation must fail for null restaurant");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("restaurant")),
                "Violation should be linked to 'restaurant' property");
    }
}
