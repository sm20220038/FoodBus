package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByCustomerIdOrderByOrderDateDesc(UUID customerId);
    boolean existsByCustomerIdAndRestaurantIdAndStatus(UUID customerId, UUID restaurantId, OrderStatus status);
}
