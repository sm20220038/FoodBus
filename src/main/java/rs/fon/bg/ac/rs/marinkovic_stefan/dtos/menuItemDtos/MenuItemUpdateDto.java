package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos;

import java.math.BigDecimal;

public record MenuItemUpdateDto(String name, String description, BigDecimal price, boolean available) {
}
