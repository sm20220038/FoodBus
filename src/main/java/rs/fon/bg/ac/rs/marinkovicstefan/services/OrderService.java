package rs.fon.bg.ac.rs.marinkovicstefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderdtos.OrderAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderdtos.OrderItemAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderdtos.OrderResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
/**
 * Service for managing customer orders.
 * Handles the whole order lifecycle: creation with total calculation,
 * status transitions, cancellation, driver assignment and order history retrieval.
 * @author Stefan Marinkovic
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DeliveryDriverRepository deliveryDriverRepository;
    private final static String orderNotFoundException = "Order doesnt exist";
    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
                        RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository,
                        DeliveryDriverRepository deliveryDriverRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.deliveryDriverRepository = deliveryDriverRepository;
    }

    /**
     * Creates a new order for a customer at a restaurant after validating all items.
     * Enforces that the order contains at least one item, that every item belongs to the
     * chosen restaurant and is currently available, and that every quantity is at least 1.
     * The unit price is copied from the menu at the moment of ordering and the order total
     * is calculated as the sum of all item subtotals. The initial status is PLACED.
     *
     * @param orderAdd OrderAddDto data transfer object containing the customer, restaurant and ordered items.
     * @return OrderResponseDto containing the information of the newly created order.
     * @throws java.lang.IllegalArgumentException If the order has no items, a quantity is lower than 1 or an item is not available.
     * @throws jakarta.persistence.EntityNotFoundException If the customer, restaurant or a menu item cannot be found.
     */
    @Transactional
    public OrderResponseDto create(OrderAddDto orderAdd){
        if (orderAdd.items() == null || orderAdd.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Customer customer = customerRepository.findById(orderAdd.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer doesnt exist"));
        Restaurant restaurant = restaurantRepository.findById(orderAdd.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant doesnt exist"));

        Order order = Order.builder()
                .orderDate(LocalDateTime.now(ZoneId.of("Europe/Belgrade")))
                .status(OrderStatus.PLACED)
                .customer(customer)
                .restaurant(restaurant)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemAddDto itemAdd : orderAdd.items()) {
            if (itemAdd.quantity() < 1) {
                throw new IllegalArgumentException("Quantity must be at least 1");
            }
            MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(itemAdd.menuItemId(), orderAdd.restaurantId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu item doesnt exist in this restaurant"));
            if (!menuItem.isAvailable()) {
                throw new IllegalArgumentException("Menu item " + menuItem.getName() + " is not available");
            }
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemAdd.quantity()));
            OrderItem orderItem = OrderItem.builder()
                    .quantity(itemAdd.quantity())
                    .unitPrice(menuItem.getPrice())
                    .subtotal(subtotal)
                    .menuItem(menuItem)
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        order.setTotal(calculateTotal(orderItems));

        return OrderResponseDto.fromEntity(orderRepository.save(order));
    }

    /**
     * Moves an order to the next status in its delivery lifecycle.
     * Only the regular transitions PLACED to PREPARING, PREPARING to ON_THE_WAY and
     * ON_THE_WAY to DELIVERED are allowed. An order cannot go on the way without an
     * assigned driver, and the driver becomes available again once the order is delivered.
     *
     * @param id unique identifier of the order whose status is updated.
     * @param newStatus name of the target status (PREPARING, ON_THE_WAY or DELIVERED).
     * @return OrderResponseDto containing the updated order information.
     * @throws jakarta.persistence.EntityNotFoundException If the order cannot be found.
     * @throws java.lang.IllegalArgumentException If the transition is not allowed, the status value is unknown or no driver is assigned when required.
     */
    @Transactional
    public OrderResponseDto updateStatus(Long id, String newStatus){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(orderNotFoundException));
        OrderStatus target = OrderStatus.valueOf(newStatus);

        OrderStatus expectedPrevious = switch (target) {
            case PREPARING -> OrderStatus.PLACED;
            case ON_THE_WAY -> OrderStatus.PREPARING;
            case DELIVERED -> OrderStatus.ON_THE_WAY;
            default -> throw new IllegalArgumentException("Status " + target + " cannot be set directly");
        };

        if (order.getStatus() != expectedPrevious) {
            throw new IllegalArgumentException("Invalid status transition from " + order.getStatus() + " to " + target);
        }
        if (target == OrderStatus.ON_THE_WAY && order.getDriver() == null) {
            throw new IllegalArgumentException("Order cannot be on the way without an assigned driver");
        }

        order.setStatus(target);
        if (target == OrderStatus.DELIVERED && order.getDriver() != null) {
            order.getDriver().setAvailable(true);
            deliveryDriverRepository.save(order.getDriver());
        }
        return OrderResponseDto.fromEntity(orderRepository.save(order));
    }

    /**
     * Cancels an order that has not been paid or sent out for delivery yet.
     * Only orders in PLACED or PREPARING status can be cancelled. If a driver was
     * already assigned, the driver is released and becomes available again.
     *
     * @param id unique identifier of the order to be cancelled.
     * @return OrderResponseDto containing the cancelled order information.
     * @throws jakarta.persistence.EntityNotFoundException If the order cannot be found.
     * @throws java.lang.IllegalArgumentException If the order was already paid or is not in a cancellable status.
     */
    @Transactional
    public OrderResponseDto cancel(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(orderNotFoundException));
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("Order in status " + order.getStatus() + " cannot be cancelled");
        }
        if (order.getPayment() != null) {
            throw new IllegalArgumentException("Paid order cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getDriver() != null) {
            order.getDriver().setAvailable(true);
            deliveryDriverRepository.save(order.getDriver());
            order.setDriver(null);
        }
        return OrderResponseDto.fromEntity(orderRepository.save(order));
    }

    /**
     * Assigns an available delivery driver to an order that is being prepared.
     * The driver becomes unavailable for other deliveries until the order is delivered
     * or cancelled.
     *
     * @param orderId unique identifier of the order that needs a driver.
     * @param driverId unique identifier of the driver to be assigned.
     * @return OrderResponseDto containing the updated order information.
     * @throws jakarta.persistence.EntityNotFoundException If the order or the driver cannot be found.
     * @throws java.lang.IllegalArgumentException If the order is not in PREPARING status, already has a driver or the driver is not available.
     */
    @Transactional
    public OrderResponseDto assignDriver(Long orderId, Long driverId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(orderNotFoundException));
        DeliveryDriver driver = deliveryDriverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver doesnt exist"));

        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("Driver can be assigned only while the order is being prepared");
        }
        if (order.getDriver() != null) {
            throw new IllegalArgumentException("Order already has an assigned driver");
        }
        if (!driver.isAvailable()) {
            throw new IllegalArgumentException("Driver " + driver.getName() + " is not available");
        }

        driver.setAvailable(false);
        deliveryDriverRepository.save(driver);
        order.setDriver(driver);
        return OrderResponseDto.fromEntity(orderRepository.save(order));
    }

    /**
     * Recalculates and stores the total price of an order from its order items.
     *
     * @param id unique identifier of the order whose total is recalculated.
     * @return BigDecimal representing the recalculated order total.
     * @throws jakarta.persistence.EntityNotFoundException If the order cannot be found.
     */
    @Transactional
    public BigDecimal recalculateTotal(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(orderNotFoundException));
        BigDecimal total = calculateTotal(order.getOrderItems());
        order.setTotal(total);
        orderRepository.save(order);
        return total;
    }

    /**
     * Retrieves the order history of a customer sorted from newest to oldest.
     *
     * @param customerId unique identifier of the customer whose history is requested.
     * @return List of OrderResponseDto objects representing all orders of the customer.
     * @throws jakarta.persistence.EntityNotFoundException If the customer cannot be found.
     */
    @Transactional
    public List<OrderResponseDto> findAllByCustomer(Long customerId){
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer doesnt exist");
        }
        List<OrderResponseDto> responseList = new ArrayList<>();
        List<Order> orders = orderRepository.findAllByCustomerIdOrderByOrderDateDesc(customerId);
        for (Order order : orders) {
            responseList.add(OrderResponseDto.fromEntity(order));
        }
        return responseList;
    }

    /**
     * Calculates the total price of an order as the sum of all order item subtotals.
     *
     * @param orderItems list of order items whose subtotals are summed.
     * @return BigDecimal representing the sum of all subtotals, or zero for an empty list.
     */
    private BigDecimal calculateTotal(List<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;
        if (orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                total = total.add(orderItem.getSubtotal());
            }
        }
        return total;
    }
}
