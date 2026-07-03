package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.MenuItem;

import java.util.List;
import java.util.Optional;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);
    boolean existsByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);
    List<MenuItem> findAllByRestaurantIdAndAvailableTrue(Long restaurantId);
}
