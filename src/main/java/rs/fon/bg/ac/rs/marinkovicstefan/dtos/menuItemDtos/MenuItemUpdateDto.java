package rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuItemDtos;

import java.math.BigDecimal;

public record MenuItemUpdateDto(String name, String description, BigDecimal price, boolean available) {
}
