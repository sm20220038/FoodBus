package rs.fon.bg.ac.rs.marinkovicstefan.dtos.paymentDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PaymentResponseDto(Long id, BigDecimal amount, String method, LocalDateTime paidAt, Long orderId) {

    public static PaymentResponseDto fromEntity(Payment payment){
        Long orderId = (payment.getOrder() != null) ? payment.getOrder().getId() : null;
        return new PaymentResponseDto(payment.getId(), payment.getAmount(), payment.getMethod().toString(),
                payment.getPaidAt(), orderId);
    }
}
