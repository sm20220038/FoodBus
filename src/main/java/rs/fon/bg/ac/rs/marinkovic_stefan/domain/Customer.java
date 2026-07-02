package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Represents a customer who orders food through the FoodBus application.
 * Holds contact and delivery information and maps all orders the customer
 * has placed along with the reviews the customer has written.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * Unique identifier for the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Full name of the customer.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Customer name is required")
    private String name;

    /**
     * Email address used for contact and identification.
     * Allowed values: Must not be blank and must be a well-formed email address. Unique in the database.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    /**
     * Contact phone number of the customer.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Phone is required")
    private String phone;

    /**
     * Delivery address of the customer.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * The list of orders this customer has placed.
     */
    @OneToMany(mappedBy = "customer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders;

    /**
     * The list of reviews this customer has written.
     */
    @OneToMany(mappedBy = "customer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews;
}
