package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.OrderStatus;

import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerIdOrderByOrderDateDesc(Long customerId);
    boolean existsByCustomerIdAndRestaurantIdAndStatus(Long customerId, Long restaurantId, OrderStatus status);
}
