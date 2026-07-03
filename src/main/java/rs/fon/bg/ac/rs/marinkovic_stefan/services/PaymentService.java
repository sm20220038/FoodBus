package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.OrderStatus;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.PayMethod;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Payment;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos.PaymentAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos.PaymentResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.OrderRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.PaymentRepository;

import java.time.LocalDateTime;

/**
 * Service for paying customer orders.
 * Creates a payment for an order with the amount equal to the order total
 * and prevents double payments and payments of cancelled orders.
 * @author Stefan Marinkovic
 */
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Pays an existing order using the given payment method.
     * The paid amount is always equal to the current order total and the
     * moment of payment is recorded automatically.
     *
     * @param paymentAdd PaymentAddDto data transfer object containing the order and the payment method.
     * @return PaymentResponseDto containing the information of the newly created payment.
     * @throws jakarta.persistence.EntityNotFoundException If the order cannot be found.
     * @throws java.lang.IllegalArgumentException If the order is cancelled, already paid or the payment method is unknown.
     */
    @Transactional
    public PaymentResponseDto pay(PaymentAddDto paymentAdd){
        Order order = orderRepository.findById(paymentAdd.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order doesnt exist"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled order cannot be paid");
        }
        if (paymentRepository.existsByOrderId(paymentAdd.orderId())) {
            throw new IllegalArgumentException("Order is already paid");
        }

        PayMethod method = PayMethod.valueOf(paymentAdd.method());
        Payment payment = Payment.builder()
                .amount(order.getTotal())
                .method(method)
                .paidAt(LocalDateTime.now())
                .order(order)
                .build();

        return PaymentResponseDto.fromEntity(paymentRepository.save(payment));
    }
}
