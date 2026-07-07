package rs.fon.bg.ac.rs.marinkovicstefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
/**
 * Represents a delivery driver who delivers orders to customers.
 * Holds contact information, the vehicle used for deliveries and the
 * current availability of the driver, and maps the orders assigned to him.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "delivery_driver")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDriver {

    /**
     * Unique identifier for the delivery driver.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the driver.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Driver name is required")
    private String name;

    /**
     * Contact phone number of the driver.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Phone is required")
    private String phone;

    /**
     * The vehicle the driver uses for deliveries (e.g. motorbike, bicycle, car).
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Vehicle is required")
    private String vehicle;

    /**
     * Indicates whether the driver is currently free to take a new delivery.
     */
    private boolean available;

    /**
     * The list of orders assigned to this driver.
     */
    @OneToMany(mappedBy = "driver")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders;
}
