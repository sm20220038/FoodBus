package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.orderDtos;

import java.util.UUID;

public record OrderItemAddDto(UUID menuItemId, int quantity) {
}
