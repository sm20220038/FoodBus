package rs.fon.bg.ac.rs.marinkovicstefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuitemdtos.MenuItemAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuitemdtos.MenuItemResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuitemdtos.MenuItemUpdateDto;
import rs.fon.bg.ac.rs.marinkovicstefan.services.MenuItemService;

import java.util.List;
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
    public ResponseEntity<Object> viewMenu(@PathVariable Long restaurantId){
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

    @PutMapping("api/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<Object> update(@PathVariable Long restaurantId, @PathVariable Long id,
                                         @RequestBody MenuItemUpdateDto menuItemUpdate){
        try {
            MenuItemResponseDto response = menuItemService.update(restaurantId, id, menuItemUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("api/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long restaurantId, @PathVariable Long id){
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
