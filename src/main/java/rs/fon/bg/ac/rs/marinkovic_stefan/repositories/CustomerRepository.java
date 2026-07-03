package rs.fon.bg.ac.rs.marinkovic_stefan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Customer;
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
}
