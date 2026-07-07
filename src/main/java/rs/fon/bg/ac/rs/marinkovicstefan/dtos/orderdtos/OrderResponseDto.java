package rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderdtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Order;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public record OrderResponseDto(Long id, LocalDateTime orderDate, String status, String customerName,
                               String restaurantName, String driverName, List<OrderItemResponseDto> items,
                               BigDecimal total) {

    public static OrderResponseDto fromEntity(Order order){
        String customerName = (order.getCustomer() != null) ? order.getCustomer().getName() : "Nema kupca";
        String restaurantName = (order.getRestaurant() != null) ? order.getRestaurant().getName() : "Nema restorana";
        String driverName = (order.getDriver() != null) ? order.getDriver().getName() : "Dostavljac nije dodeljen";
        List<OrderItemResponseDto> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                items.add(OrderItemResponseDto.fromEntity(orderItem));
            }
        }
        return new OrderResponseDto(order.getId(), order.getOrderDate(), order.getStatus().toString(),
                customerName, restaurantName, driverName, items, order.getTotal());
    }
}
