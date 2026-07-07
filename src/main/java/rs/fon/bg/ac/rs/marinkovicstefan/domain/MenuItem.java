package rs.fon.bg.ac.rs.marinkovicstefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
/**
 * Represents a single dish or product on a restaurant menu.
 * Holds the price and availability of the item and maps the restaurant
 * that offers it along with the order items in which it appears.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    /**
     * Unique identifier for the menu item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the dish or product.
     * Allowed values: Must not be blank.
     */
    @NotBlank(message = "Menu item name is required")
    private String name;

    /**
     * Optional description of the dish (ingredients, size, etc.).
     */
    private String description;

    /**
     * Price of a single unit of the menu item.
     * Allowed values: Must not be null and must be positive.
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    /**
     * Indicates whether the menu item can currently be ordered.
     */
    private boolean available;

    /**
     * The restaurant that offers this menu item.
     * This field is mandatory and lazy-loaded.
     */
    @NotNull(message = "Restaurant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurant restaurant;

    /**
     * The list of order items in which this menu item appears.
     */
    @OneToMany(mappedBy = "menuItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> orderItems;
}
