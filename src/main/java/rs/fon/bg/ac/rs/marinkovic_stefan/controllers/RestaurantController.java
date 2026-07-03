package rs.fon.bg.ac.rs.marinkovic_stefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.restaurantDtos.RestaurantResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.services.RestaurantService;

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
}
