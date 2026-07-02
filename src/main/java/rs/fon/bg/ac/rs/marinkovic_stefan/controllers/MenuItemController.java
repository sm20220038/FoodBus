package rs.fon.bg.ac.rs.marinkovic_stefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos.MenuItemAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos.MenuItemResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.services.MenuItemService;

import java.util.List;
import java.util.UUID;

@RestController
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("api/menu-items")
    public ResponseEntity<Object> create(@RequestBody MenuItemAddDto menuItemAdd){
        try {
            MenuItemResponseDto response = menuItemService.create(menuItemAdd);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Create menu item unsuccessful");
        }
    }

    @GetMapping("api/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<Object> viewMenu(@PathVariable UUID restaurantId){
        try {
            List<MenuItemResponseDto> response = menuItemService.viewMenu(restaurantId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("api/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID restaurantId, @PathVariable UUID id){
        try {
            menuItemService.delete(restaurantId, id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Menu item deleted");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Delete menu item unsuccessful");
        }
    }
}
