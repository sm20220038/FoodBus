package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.DeliveryDriver;

import java.util.UUID;

public interface DeliveryDriverRepository extends JpaRepository<DeliveryDriver, UUID> {
}
