package rs.fon.bg.ac.rs.marinkovicstefan.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.restaurantDtos.RestaurantResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.RestaurantRepository;

import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant firstRestaurant;
    private Restaurant secondRestaurant;

    @BeforeEach
    void setUp() {
        firstRestaurant = Restaurant.builder()
                .id(1L)
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(4.5)
                .email("kontakt@burgerhouse.rs")
                .build();

        secondRestaurant = Restaurant.builder()
                .id(2L)
                .name("Pizza Bar")
                .address("Knez Mihailova 5, Beograd")
                .cuisine("Italijanska")
                .rating(4.2)
                .email("info@pizzabar.rs")
                .build();
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @Test
    @DisplayName("Should return all restaurants matching the given filter criteria")
    void filter_ValidCriteria_ReturnsMatchingRestaurants() {
        when(restaurantRepository.filter("Roostilj", 4.0, null)).thenReturn(List.of(firstRestaurant));

        List<RestaurantResponseDto> result = restaurantService.filter("Roostilj", 4.0, null);

        assertEquals(1, result.size());
        assertEquals("Burger House", result.get(0).name());
        assertEquals(4.5, result.get(0).rating());
    }

    @Test
    @DisplayName("Should return all restaurants when no filter criteria are given")
    void filter_NoCriteria_ReturnsAllRestaurants() {
        when(restaurantRepository.filter(null, null, null)).thenReturn(List.of(firstRestaurant, secondRestaurant));

        List<RestaurantResponseDto> result = restaurantService.filter(null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return an empty list when no restaurant matches the criteria")
    void filter_NoMatches_ReturnsEmptyList() {
        when(restaurantRepository.filter("Meksicka", null, null)).thenReturn(Collections.emptyList());

        List<RestaurantResponseDto> result = restaurantService.filter("Meksicka", null, null);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.5, -1.0, 5.1, 10.0})
    @DisplayName("Should throw IllegalArgumentException when the minimum rating is outside the 0 to 5 range")
    void filter_InvalidMinRating_ThrowsIllegalArgumentException(double invalidMinRating) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                restaurantService.filter(null, invalidMinRating, null)
        );

        assertEquals("Minimum rating must be between 0 and 5", exception.getMessage());
        verifyNoInteractions(restaurantRepository);
    }
}
