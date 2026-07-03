package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Payment;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
}
