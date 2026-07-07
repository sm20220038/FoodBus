package rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuitemdtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.MenuItem;

import java.math.BigDecimal;
public record MenuItemResponseDto(Long id, String name, String description, BigDecimal price, boolean available,
                                  String restaurantName) {

    public static MenuItemResponseDto fromEntity(MenuItem menuItem){
        String restaurantName = (menuItem.getRestaurant() != null) ? menuItem.getRestaurant().getName() : "Nema restorana";
        return new MenuItemResponseDto(menuItem.getId(), menuItem.getName(), menuItem.getDescription(),
                menuItem.getPrice(), menuItem.isAvailable(), restaurantName);
    }
}
