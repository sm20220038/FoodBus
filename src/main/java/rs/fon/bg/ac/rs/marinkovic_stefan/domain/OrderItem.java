package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a single line of an order connecting the order with a menu item.
 * Stores the ordered quantity and the unit price at the moment of ordering,
 * so later menu price changes do not affect already placed orders.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    /**
     * Unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Number of units of the menu item in this order line.
     * Allowed values: Must be at least 1.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    /**
     * Price of a single unit at the moment the order was placed.
     * Allowed values: Must not be null and must be positive.
     */
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    /**
     * Total price of this order line calculated as quantity multiplied by unit price.
     * Allowed values: Must not be null and cannot be negative.
     */
    @NotNull(message = "Subtotal is required")
    @PositiveOrZero(message = "Subtotal cannot be negative")
    private BigDecimal subtotal;

    /**
     * The order this line belongs to.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    /**
     * The menu item that was ordered in this line.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Menu item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MenuItem menuItem;
}
