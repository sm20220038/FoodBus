package rs.fon.bg.ac.rs.marinkovicstefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.Review;

import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRestaurantId(Long restaurantId);
}
