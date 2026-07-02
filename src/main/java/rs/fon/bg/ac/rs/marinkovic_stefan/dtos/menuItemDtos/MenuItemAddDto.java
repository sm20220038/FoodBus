package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.MenuItem;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemAddDto(String name, String description, BigDecimal price, boolean available, UUID restaurantId) {
    public MenuItem toEntity(){
        return MenuItem.builder().name(name).description(description).price(price).available(available).build();
    }
}
