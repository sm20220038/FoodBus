package rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuItemDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.MenuItem;

import java.math.BigDecimal;
public record MenuItemAddDto(String name, String description, BigDecimal price, boolean available, Long restaurantId) {
    public MenuItem toEntity(){
        return MenuItem.builder().name(name).description(description).price(price).available(available).build();
    }
}
