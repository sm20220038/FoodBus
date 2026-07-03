package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Review;

import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRestaurantId(Long restaurantId);
}
