package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.OrderStatus;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Payment;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos.PaymentAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos.PaymentResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.OrderRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID orderId;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        sampleOrder = Order.builder()
                .id(orderId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .total(new BigDecimal("1180.00"))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should successfully pay an order with the amount equal to the order total")
    void pay_ValidOrder_SavesAndReturnsResponseDto() {
        PaymentAddDto addDto = new PaymentAddDto(orderId, "CARD");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponseDto result = paymentService.pay(addDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1180.00"), result.amount());
        assertEquals("CARD", result.method());
        assertEquals(orderId, result.orderId());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when paying an order that does not exist")
    void pay_MissingOrder_ThrowsEntityNotFoundException() {
        PaymentAddDto addDto = new PaymentAddDto(orderId, "CARD");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.pay(addDto));
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when paying a cancelled order")
    void pay_CancelledOrder_ThrowsIllegalArgumentException() {
        sampleOrder.setStatus(OrderStatus.CANCELLED);
        PaymentAddDto addDto = new PaymentAddDto(orderId, "CASH");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentService.pay(addDto)
        );

        assertEquals("Cancelled order cannot be paid", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the order is already paid")
    void pay_AlreadyPaidOrder_ThrowsIllegalArgumentException() {
        PaymentAddDto addDto = new PaymentAddDto(orderId, "PAYPAL");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentService.pay(addDto)
        );

        assertEquals("Order is already paid", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the payment method is unknown")
    void pay_UnknownMethod_ThrowsIllegalArgumentException() {
        PaymentAddDto addDto = new PaymentAddDto(orderId, "BITCOIN");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> paymentService.pay(addDto));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
