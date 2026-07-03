package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.paymentDtos;

import java.util.UUID;

public record PaymentAddDto(UUID orderId, String method) {
}
