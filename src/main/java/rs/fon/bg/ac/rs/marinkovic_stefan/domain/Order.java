package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Represents a single customer order in the FoodBus application.
 * Encapsulates the ordered items, the calculated total price, the current
 * delivery status, and maps the customer, restaurant, payment and delivery
 * driver associated with the order.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the order was created.
     * Allowed values: Must not be null.
     */
    @NotNull(message = "Order date is required")
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    /**
     * The current status of the order in its lifecycle.
     * Allowed values: Must not be null. Value must be one of the OrderStatus enum constants.
     */
    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * The total price of the order calculated as the sum of all order item subtotals.
     * Allowed values: Cannot be negative.
     */
    @PositiveOrZero(message = "Total cannot be negative")
    private BigDecimal total;

    /**
     * The customer who placed the order.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    /**
     * The restaurant that received the order.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Restaurant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurant restaurant;

    /**
     * The list of items that make up this order.
     * Any changes to the order will cascade to its order items.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> orderItems;

    /**
     * The payment that settled this order, or null if the order has not been paid yet.
     */
    @OneToOne(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Payment payment;

    /**
     * The delivery driver assigned to this order, or null if no driver has been assigned yet.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DeliveryDriver driver;
}
