package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Customer;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.OrderStatus;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Review;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.reviewDtos.ReviewAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.reviewDtos.ReviewResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.CustomerRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.OrderRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.RestaurantRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.ReviewRepository;

import java.util.List;

/**
 * Service for managing restaurant reviews.
 * Allows customers to review restaurants they have ordered from and keeps
 * the average restaurant rating up to date with every new review.
 * @author Stefan Marinkovic
 */
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, CustomerRepository customerRepository,
                         RestaurantRepository restaurantRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new review of a restaurant written by a customer.
     * Enforces that the customer has at least one delivered order from the
     * restaurant, then recalculates the restaurant average rating.
     *
     * @param reviewAdd ReviewAddDto data transfer object containing the customer, restaurant, rating and comment.
     * @return ReviewResponseDto containing the information of the newly created review.
     * @throws jakarta.persistence.EntityNotFoundException If the customer or the restaurant cannot be found.
     * @throws java.lang.IllegalArgumentException If the customer has no delivered order from the restaurant.
     */
    @Transactional
    public ReviewResponseDto create(ReviewAddDto reviewAdd){
        Customer customer = customerRepository.findById(reviewAdd.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer doesnt exist"));
        Restaurant restaurant = restaurantRepository.findById(reviewAdd.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant doesnt exist"));

        if (!orderRepository.existsByCustomerIdAndRestaurantIdAndStatus(
                reviewAdd.customerId(), reviewAdd.restaurantId(), OrderStatus.DELIVERED)) {
            throw new IllegalArgumentException("Customer can review only restaurants with a delivered order");
        }

        Review review = reviewAdd.toEntity();
        review.setCustomer(customer);
        review.setRestaurant(restaurant);
        Review saved = reviewRepository.save(review);

        updateRestaurantRating(restaurant);

        return ReviewResponseDto.fromEntity(saved);
    }

    /**
     * Recalculates the average rating of a restaurant from all of its reviews
     * and stores the new value.
     *
     * @param restaurant restaurant whose average rating is recalculated.
     */
    private void updateRestaurantRating(Restaurant restaurant) {
        List<Review> reviews = reviewRepository.findAllByRestaurantId(restaurant.getId());
        if (reviews.isEmpty()) {
            return;
        }
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        restaurant.setRating(sum / reviews.size());
        restaurantRepository.save(restaurant);
    }
}
