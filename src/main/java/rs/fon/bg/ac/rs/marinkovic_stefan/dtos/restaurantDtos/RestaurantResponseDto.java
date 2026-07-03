package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.restaurantDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Restaurant;
public record RestaurantResponseDto(Long id, String name, String address, String cuisine, double rating, String email) {

    public static RestaurantResponseDto fromEntity(Restaurant restaurant){
        return new RestaurantResponseDto(restaurant.getId(), restaurant.getName(), restaurant.getAddress(),
                restaurant.getCuisine(), restaurant.getRating(), restaurant.getEmail());
    }
}
