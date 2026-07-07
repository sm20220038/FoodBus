package rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderDtos;

import java.util.List;
public record OrderAddDto(Long customerId, Long restaurantId, List<OrderItemAddDto> items) {
}
