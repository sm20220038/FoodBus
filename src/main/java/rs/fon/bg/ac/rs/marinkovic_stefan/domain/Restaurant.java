package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
/**
 * Represents a restaurant that offers food through the FoodBus application.
 * Encapsulates basic information about the restaurant, its average rating,
 * and maps the menu items it offers, the orders it receives and the reviews
 * customers have left for it.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "restaurant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    /**
     * Unique identifier for the restaurant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the restaurant.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Restaurant name is required")
    private String name;

    /**
     * Street address of the restaurant.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * Type of cuisine the restaurant serves (e.g. grill, Italian, Chinese).
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Cuisine is required")
    private String cuisine;

    /**
     * Average rating of the restaurant calculated from customer reviews.
     * Allowed values: Between 0.0 and 5.0.
     */
    @DecimalMin(value = "0.0", message = "Rating cannot be lower than 0")
    @DecimalMax(value = "5.0", message = "Rating cannot be higher than 5")
    private double rating;

    /**
     * Contact email address of the restaurant.
     * Allowed values: Must not be blank and must be a well-formed email address. Unique in the database.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    /**
     * The list of menu items this restaurant offers.
     */
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MenuItem> menuItems;

    /**
     * The list of orders this restaurant has received.
     */
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders;

    /**
     * The list of reviews customers have left for this restaurant.
     */
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews;
}
