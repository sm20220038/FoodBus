package rs.fon.bg.ac.rs.marinkovicstefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.Restaurant;

import java.util.List;
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r WHERE " +
            "(:cuisine IS NULL OR LOWER(r.cuisine) = LOWER(:cuisine)) " +
            "AND (:minRating IS NULL OR r.rating >= :minRating) " +
            "AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Restaurant> filter(
            @Param("cuisine") String cuisine,
            @Param("minRating") Double minRating,
            @Param("name") String name
    );

    boolean existsByEmail(String email);
}
