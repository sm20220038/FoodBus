package rs.fon.bg.ac.rs.marinkovic_stefan.domain;

/**
 * Represents the supported methods for paying an order.
 * @author Stefan Marinkovic
 */
public enum PayMethod {
    /**
     * Payment in cash on delivery.
     */
    CASH,
    /**
     * Payment with a credit or debit card.
     */
    CARD,
    /**
     * Payment through the PayPal service.
     */
    PAYPAL
}
