package rs.fon.bg.ac.rs.marinkovicstefan.domain;

/**
 * Represents the lifecycle status of a customer order.
 * The regular flow is PLACED, PREPARING, ON_THE_WAY and finally DELIVERED,
 * while CANCELLED marks an order that was aborted before delivery.
 * @author Stefan Marinkovic
 */
public enum OrderStatus {
    /**
     * The order has been created by the customer and is waiting for the restaurant.
     */
    PLACED,
    /**
     * The restaurant has accepted the order and is preparing the food.
     */
    PREPARING,
    /**
     * The order has left the restaurant and is being delivered by a driver.
     */
    ON_THE_WAY,
    /**
     * The order has been successfully delivered to the customer.
     */
    DELIVERED,
    /**
     * The order has been cancelled and will not be delivered.
     */
    CANCELLED
}
