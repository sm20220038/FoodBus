package rs.fon.bg.ac.rs.marinkovicstefan.dtos.orderDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDto(String menuItemName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {

    public static OrderItemResponseDto fromEntity(OrderItem orderItem){
        String menuItemName = (orderItem.getMenuItem() != null) ? orderItem.getMenuItem().getName() : "Nema stavke menija";
        return new OrderItemResponseDto(menuItemName, orderItem.getQuantity(), orderItem.getUnitPrice(), orderItem.getSubtotal());
    }
}
