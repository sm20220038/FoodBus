package rs.fon.bg.ac.rs.marinkovicstefan.dtos.restaurantdtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Restaurant;

public record RestaurantAddDto(String name, String address, String cuisine, String email) {
    public Restaurant toEntity(){
        return Restaurant.builder().name(name).address(address).cuisine(cuisine).email(email).rating(0.0).build();
    }
}
