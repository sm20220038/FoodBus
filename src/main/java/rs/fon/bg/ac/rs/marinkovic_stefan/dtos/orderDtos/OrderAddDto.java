package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.orderDtos;

import java.util.List;
import java.util.UUID;

public record OrderAddDto(UUID customerId, UUID restaurantId, List<OrderItemAddDto> items) {
}
