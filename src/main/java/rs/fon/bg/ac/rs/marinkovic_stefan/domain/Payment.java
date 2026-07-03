package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Represents a payment that settles a single order.
 * Stores the paid amount, the payment method and the moment of payment.
 * Each order can have at most one payment.
 * @author Stefan Marinkovic
 */
@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    /**
     * Unique identifier for the payment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The amount of money that was paid, equal to the order total.
     * Allowed values: Must not be null and must be positive.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /**
     * The method used to pay the order.
     * Allowed values: Must not be null. Value must be one of the PayMethod enum constants.
     */
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayMethod method;

    /**
     * The date and time when the payment was made.
     * Allowed values: Must not be null.
     */
    @NotNull(message = "Payment date is required")
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * The order settled by this payment.
     * This field is mandatory, unique and lazy-loaded.
     */
    @NotNull(message = "Order is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;
}
