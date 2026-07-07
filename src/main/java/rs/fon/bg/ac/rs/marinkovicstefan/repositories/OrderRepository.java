package rs.fon.bg.ac.rs.marinkovicstefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.OrderStatus;

import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerIdOrderByOrderDateDesc(Long customerId);
    boolean existsByCustomerIdAndRestaurantIdAndStatus(Long customerId, Long restaurantId, OrderStatus status);
}
