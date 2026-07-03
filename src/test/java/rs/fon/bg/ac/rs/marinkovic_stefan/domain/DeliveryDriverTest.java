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
import static org.junit.jupiter.api.Assertions.*;

class DeliveryDriverTest {

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
            "Nikola Nikolic, Motor, true",
            "Petar Petrovic, Bicikl, false",
            "Jovan Jovanovic, Automobil, true"
    })
    @DisplayName("Should pass validation with valid delivery driver parameters")
    void validate_ValidDriver_NoViolations(String name, String vehicle, boolean available) {
        DeliveryDriver driver = DeliveryDriver.builder()
                .id(1L)
                .name(name)
                .phone("0651234567")
                .vehicle(vehicle)
                .available(available)
                .build();

        Set<ConstraintViolation<DeliveryDriver>> violations = validator.validate(driver);
        assertTrue(violations.isEmpty(), "Validation should pass for valid drivers");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when driver name is blank")
    void validate_BlankName_HasViolations(String invalidName) {
        DeliveryDriver driver = DeliveryDriver.builder()
                .name(invalidName)
                .phone("0651234567")
                .vehicle("Motor")
                .available(true)
                .build();

        Set<ConstraintViolation<DeliveryDriver>> violations = validator.validate(driver);

        assertFalse(violations.isEmpty(), "Validation must fail for blank name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Violation should be linked to 'name' property");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when driver phone is blank")
    void validate_BlankPhone_HasViolations(String invalidPhone) {
        DeliveryDriver driver = DeliveryDriver.builder()
                .name("Nikola Nikolic")
                .phone(invalidPhone)
                .vehicle("Motor")
                .available(true)
                .build();

        Set<ConstraintViolation<DeliveryDriver>> violations = validator.validate(driver);

        assertFalse(violations.isEmpty(), "Validation must fail for blank phone");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")),
                "Violation should be linked to 'phone' property");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should fail validation when vehicle is blank")
    void validate_BlankVehicle_HasViolations(String invalidVehicle) {
        DeliveryDriver driver = DeliveryDriver.builder()
                .name("Nikola Nikolic")
                .phone("0651234567")
                .vehicle(invalidVehicle)
                .available(true)
                .build();

        Set<ConstraintViolation<DeliveryDriver>> violations = validator.validate(driver);

        assertFalse(violations.isEmpty(), "Validation must fail for blank vehicle");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("vehicle")),
                "Violation should be linked to 'vehicle' property");
    }
}
