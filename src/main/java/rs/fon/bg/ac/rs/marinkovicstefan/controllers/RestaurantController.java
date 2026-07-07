package rs.fon.bg.ac.rs.marinkovicstefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.restaurantDtos.RestaurantAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.restaurantDtos.RestaurantResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.services.RestaurantService;

import java.util.List;

@RestController
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("api/restaurants")
    public ResponseEntity<Object> filter(@RequestParam(required = false) String cuisine,
                                         @RequestParam(required = false) Double minRating,
                                         @RequestParam(required = false) String name){
        try {
            List<RestaurantResponseDto> response = restaurantService.filter(cuisine, minRating, name);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("api/restaurants")
    public ResponseEntity<Object> create(@RequestBody RestaurantAddDto restaurantAdd){
        try {
            RestaurantResponseDto response = restaurantService.create(restaurantAdd);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("api/restaurants/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody RestaurantAddDto restaurantUpdate){
        try {
            RestaurantResponseDto response = restaurantService.update(id, restaurantUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("api/restaurants/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            restaurantService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Restaurant deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
