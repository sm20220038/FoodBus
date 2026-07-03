package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Customer;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.customerDtos.CustomerAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.customerDtos.CustomerResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.CustomerRepository;

/**
 * Service for managing customers.
 * Handles registering new customers and updating or deleting existing ones.
 * @author Stefan Marinkovic
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a new customer.
     * Enforces that the email address is not already used by another customer.
     *
     * @param customerAdd CustomerAddDto data transfer object containing the customer details.
     * @return CustomerResponseDto containing the information of the newly created customer.
     * @throws java.lang.IllegalArgumentException If a customer with the same email already exists.
     */
    @Transactional
    public CustomerResponseDto create(CustomerAddDto customerAdd){
        if (customerRepository.existsByEmail(customerAdd.email())) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }
        return CustomerResponseDto.fromEntity(customerRepository.save(customerAdd.toEntity()));
    }

    /**
     * Updates the details of an existing customer.
     *
     * @param id unique identifier of the customer to update.
     * @param customerUpdate CustomerAddDto containing the new customer details.
     * @return CustomerResponseDto containing the updated customer information.
     * @throws jakarta.persistence.EntityNotFoundException If the customer cannot be found.
     */
    @Transactional
    public CustomerResponseDto update(Long id, CustomerAddDto customerUpdate){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer doesnt exist"));
        customer.setName(customerUpdate.name());
        customer.setEmail(customerUpdate.email());
        customer.setPhone(customerUpdate.phone());
        customer.setAddress(customerUpdate.address());
        return CustomerResponseDto.fromEntity(customerRepository.save(customer));
    }

    /**
     * Deletes an existing customer.
     *
     * @param id unique identifier of the customer to delete.
     * @throws jakarta.persistence.EntityNotFoundException If the customer cannot be found.
     */
    @Transactional
    public void delete(Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer doesnt exist"));
        customerRepository.delete(customer);
    }
}
