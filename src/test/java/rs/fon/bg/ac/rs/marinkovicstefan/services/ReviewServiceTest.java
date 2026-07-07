package rs.fon.bg.ac.rs.marinkovicstefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos.ReviewAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos.ReviewResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.CustomerRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.OrderRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.RestaurantRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Long customerId;
    private Long restaurantId;
    private Customer sampleCustomer;
    private Restaurant sampleRestaurant;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        restaurantId = 2L;

        sampleCustomer = Customer.builder()
                .id(customerId)
                .name("Marko Markovic")
                .email("marko.markovic@gmail.com")
                .phone("0641234567")
                .address("Jove Ilica 154, Beograd")
                .build();

        sampleRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(0.0)
                .email("kontakt@burgerhouse.rs")
                .build();
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @Test
    @DisplayName("Should create a review and update the restaurant average rating")
    void create_ValidReview_SavesAndUpdatesRestaurantRating() {
        ReviewAddDto addDto = new ReviewAddDto(customerId, restaurantId, 5, "Odlicna hrana, brza dostava!");

        Review firstReview = Review.builder()
                .rating(5).comment("Odlicna hrana, brza dostava!").createdAt(LocalDateTime.now())
                .customer(sampleCustomer).restaurant(sampleRestaurant).build();
        Review secondReview = Review.builder()
                .rating(3).comment("Solidno.").createdAt(LocalDateTime.now())
                .customer(sampleCustomer).restaurant(sampleRestaurant).build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(orderRepository.existsByCustomerIdAndRestaurantIdAndStatus(customerId, restaurantId, OrderStatus.DELIVERED))
                .thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(firstReview, secondReview));

        ReviewResponseDto result = reviewService.create(addDto);

        assertNotNull(result);
        assertEquals(5, result.rating());
        assertEquals("Burger House", result.restaurantName());
        assertEquals(4.0, sampleRestaurant.getRating(), "Restaurant rating should be the average of all reviews");

        verify(reviewRepository).save(any(Review.class));
        verify(restaurantRepository).save(sampleRestaurant);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the customer has no delivered order from the restaurant")
    void create_NoDeliveredOrder_ThrowsIllegalArgumentException() {
        ReviewAddDto addDto = new ReviewAddDto(customerId, restaurantId, 5, "Odlicna hrana!");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(orderRepository.existsByCustomerIdAndRestaurantIdAndStatus(customerId, restaurantId, OrderStatus.DELIVERED))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reviewService.create(addDto)
        );

        assertEquals("Customer can review only restaurants with a delivered order", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the customer does not exist")
    void create_MissingCustomer_ThrowsEntityNotFoundException() {
        ReviewAddDto addDto = new ReviewAddDto(customerId, restaurantId, 5, "Odlicna hrana!");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.create(addDto));
        verifyNoInteractions(reviewRepository);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the restaurant does not exist")
    void create_MissingRestaurant_ThrowsEntityNotFoundException() {
        ReviewAddDto addDto = new ReviewAddDto(customerId, restaurantId, 5, "Odlicna hrana!");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.create(addDto));
        verifyNoInteractions(reviewRepository);
    }
}
