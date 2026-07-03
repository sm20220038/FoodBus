package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(UUID id, BigDecimal amount, String method, LocalDateTime paidAt, UUID orderId) {

    public static PaymentResponseDto fromEntity(Payment payment){
        UUID orderId = (payment.getOrder() != null) ? payment.getOrder().getId() : null;
        return new PaymentResponseDto(payment.getId(), payment.getAmount(), payment.getMethod().toString(),
                payment.getPaidAt(), orderId);
    }
}
