package rs.fon.bg.ac.rs.marinkovicstefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderDtos.OrderAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderDtos.OrderItemAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderDtos.OrderResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private DeliveryDriverRepository deliveryDriverRepository;

    @InjectMocks
    private OrderService orderService;

    private Long customerId;
    private Long restaurantId;
    private Long menuItemId;
    private Long orderId;
    private Long driverId;
    private Customer sampleCustomer;
    private Restaurant sampleRestaurant;
    private MenuItem sampleMenuItem;
    private DeliveryDriver sampleDriver;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        restaurantId = 2L;
        menuItemId = 3L;
        orderId = 4L;
        driverId = 5L;

        sampleCustomer = Customer.builder()
                .id(customerId)
                .name("Marko Markovic")
                .email("marko.markovic@gmail.com")
                .phone("0641234567")
                .address("Jove Ilica 154, Beograd")
                .build();

        sampleRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(4.5)
                .email("kontakt@burgerhouse.rs")
                .build();

        sampleMenuItem = MenuItem.builder()
                .id(menuItemId)
                .name("Cheeseburger")
                .price(new BigDecimal("590.00"))
                .available(true)
                .restaurant(sampleRestaurant)
                .build();

        sampleDriver = DeliveryDriver.builder()
                .id(driverId)
                .name("Nikola Nikolic")
                .phone("0651234567")
                .vehicle("Motor")
                .available(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    private Order buildOrder(OrderStatus status) {
        Order order = Order.builder()
                .id(orderId)
                .orderDate(LocalDateTime.now())
                .status(status)
                .total(new BigDecimal("1180.00"))
                .customer(sampleCustomer)
                .restaurant(sampleRestaurant)
                .build();
        OrderItem orderItem = OrderItem.builder()
                .quantity(2)
                .unitPrice(new BigDecimal("590.00"))
                .subtotal(new BigDecimal("1180.00"))
                .order(order)
                .menuItem(sampleMenuItem)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return order;
    }

    @Test
    @DisplayName("Should create an order and calculate the total from all order items")
    void create_ValidOrder_CalculatesTotalAndSaves() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, List.of(new OrderItemAddDto(menuItemId, 2)));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)).thenReturn(Optional.of(sampleMenuItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.create(addDto);

        assertNotNull(result);
        assertEquals("PLACED", result.status());
        assertEquals("Marko Markovic", result.customerName());
        assertEquals("Burger House", result.restaurantName());
        assertEquals(1, result.items().size());
        assertEquals(new BigDecimal("1180.00"), result.total());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the order has no items")
    void create_EmptyItems_ThrowsIllegalArgumentException() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.create(addDto)
        );

        assertEquals("Order must contain at least one item", exception.getMessage());
        verifyNoInteractions(orderRepository, customerRepository, restaurantRepository, menuItemRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the item list is null")
    void create_NullItems_ThrowsIllegalArgumentException() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, null);

        assertThrows(IllegalArgumentException.class, () -> orderService.create(addDto));
        verifyNoInteractions(orderRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the quantity is lower than 1")
    void create_InvalidQuantity_ThrowsIllegalArgumentException() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, List.of(new OrderItemAddDto(menuItemId, 0)));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));

        assertThrows(IllegalArgumentException.class, () -> orderService.create(addDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the menu item is not available")
    void create_UnavailableMenuItem_ThrowsIllegalArgumentException() {
        sampleMenuItem.setAvailable(false);
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, List.of(new OrderItemAddDto(menuItemId, 1)));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)).thenReturn(Optional.of(sampleMenuItem));

        assertThrows(IllegalArgumentException.class, () -> orderService.create(addDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the menu item does not belong to the restaurant")
    void create_MenuItemFromOtherRestaurant_ThrowsEntityNotFoundException() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, List.of(new OrderItemAddDto(menuItemId, 1)));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(sampleCustomer));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.create(addDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the customer does not exist")
    void create_MissingCustomer_ThrowsEntityNotFoundException() {
        OrderAddDto addDto = new OrderAddDto(customerId, restaurantId, List.of(new OrderItemAddDto(menuItemId, 1)));

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.create(addDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should move the order from PLACED to PREPARING")
    void updateStatus_PlacedToPreparing_UpdatesStatus() {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.updateStatus(orderId, "PREPARING");

        assertEquals("PREPARING", result.status());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when moving to ON_THE_WAY without an assigned driver")
    void updateStatus_OnTheWayWithoutDriver_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PREPARING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(orderId, "ON_THE_WAY"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should move the order to ON_THE_WAY when a driver is assigned")
    void updateStatus_OnTheWayWithDriver_UpdatesStatus() {
        Order order = buildOrder(OrderStatus.PREPARING);
        order.setDriver(sampleDriver);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.updateStatus(orderId, "ON_THE_WAY");

        assertEquals("ON_THE_WAY", result.status());
        assertEquals("Nikola Nikolic", result.driverName());
    }

    @Test
    @DisplayName("Should free the driver when the order is delivered")
    void updateStatus_Delivered_FreesDriver() {
        Order order = buildOrder(OrderStatus.ON_THE_WAY);
        sampleDriver.setAvailable(false);
        order.setDriver(sampleDriver);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.updateStatus(orderId, "DELIVERED");

        assertEquals("DELIVERED", result.status());
        assertTrue(sampleDriver.isAvailable(), "Driver should become available again after delivery");
        verify(deliveryDriverRepository).save(sampleDriver);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for an invalid status transition")
    void updateStatus_InvalidTransition_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(orderId, "DELIVERED"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLACED", "CANCELLED"})
    @DisplayName("Should throw IllegalArgumentException when a status cannot be set directly")
    void updateStatus_DirectlyForbiddenStatus_ThrowsIllegalArgumentException(String target) {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(orderId, target));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for an unknown status value")
    void updateStatus_UnknownStatus_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(orderId, "FINISHED"));
    }

    @Test
    @DisplayName("Should cancel an order that is still in PLACED status")
    void cancel_PlacedOrder_SetsCancelledStatus() {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.cancel(orderId);

        assertEquals("CANCELLED", result.status());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when cancelling a delivered order")
    void cancel_DeliveredOrder_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.cancel(orderId));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when cancelling a paid order")
    void cancel_PaidOrder_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PLACED);
        order.setPayment(Payment.builder().amount(new BigDecimal("1180.00")).build());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.cancel(orderId)
        );

        assertEquals("Paid order cannot be cancelled", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should assign an available driver to an order in PREPARING status")
    void assignDriver_ValidOrderAndDriver_AssignsDriver() {
        Order order = buildOrder(OrderStatus.PREPARING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryDriverRepository.findById(driverId)).thenReturn(Optional.of(sampleDriver));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto result = orderService.assignDriver(orderId, driverId);

        assertEquals("Nikola Nikolic", result.driverName());
        assertFalse(sampleDriver.isAvailable(), "Driver should become unavailable after assignment");
        verify(deliveryDriverRepository).save(sampleDriver);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the order is not in PREPARING status")
    void assignDriver_OrderNotPreparing_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PLACED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryDriverRepository.findById(driverId)).thenReturn(Optional.of(sampleDriver));

        assertThrows(IllegalArgumentException.class, () -> orderService.assignDriver(orderId, driverId));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the order already has a driver")
    void assignDriver_AlreadyAssigned_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PREPARING);
        order.setDriver(DeliveryDriver.builder().id(6L).name("Petar Petrovic").build());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryDriverRepository.findById(driverId)).thenReturn(Optional.of(sampleDriver));

        assertThrows(IllegalArgumentException.class, () -> orderService.assignDriver(orderId, driverId));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the driver is not available")
    void assignDriver_DriverNotAvailable_ThrowsIllegalArgumentException() {
        Order order = buildOrder(OrderStatus.PREPARING);
        sampleDriver.setAvailable(false);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryDriverRepository.findById(driverId)).thenReturn(Optional.of(sampleDriver));

        assertThrows(IllegalArgumentException.class, () -> orderService.assignDriver(orderId, driverId));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should recalculate the order total from its order items")
    void recalculateTotal_ExistingOrder_ReturnsSumOfSubtotals() {
        Order order = buildOrder(OrderStatus.PLACED);
        order.setTotal(BigDecimal.ZERO);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal total = orderService.recalculateTotal(orderId);

        assertEquals(new BigDecimal("1180.00"), total);
        assertEquals(new BigDecimal("1180.00"), order.getTotal());
    }

    @Test
    @DisplayName("Should return the order history of an existing customer")
    void findAllByCustomer_ExistingCustomer_ReturnsOrders() {
        Order first = buildOrder(OrderStatus.DELIVERED);
        Order second = buildOrder(OrderStatus.PLACED);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(orderRepository.findAllByCustomerIdOrderByOrderDateDesc(customerId)).thenReturn(List.of(first, second));

        List<OrderResponseDto> result = orderService.findAllByCustomer(customerId);

        assertEquals(2, result.size());
        assertEquals("DELIVERED", result.get(0).status());
        assertEquals("PLACED", result.get(1).status());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException for the order history of a missing customer")
    void findAllByCustomer_MissingCustomer_ThrowsEntityNotFoundException() {
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.findAllByCustomer(customerId));
        verifyNoInteractions(orderRepository);
    }
}
