package rs.fon.bg.ac.rs.marinkovicstefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
/**
 * Represents a review a customer has left for a restaurant after a delivered order.
 * Stores the numeric rating and an optional comment, and maps the customer
 * who wrote the review and the restaurant being reviewed.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    /**
     * Unique identifier for the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numeric rating the customer gave to the restaurant.
     * Allowed values: Between 1 and 5.
     */
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be higher than 5")
    private int rating;

    /**
     * Optional textual comment describing the customer experience.
     */
    private String comment;

    /**
     * The date and time when the review was created.
     * Allowed values: Must not be null.
     */
    @NotNull(message = "Creation date is required")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * The customer who wrote the review.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    /**
     * The restaurant this review refers to.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Restaurant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurant restaurant;
}
