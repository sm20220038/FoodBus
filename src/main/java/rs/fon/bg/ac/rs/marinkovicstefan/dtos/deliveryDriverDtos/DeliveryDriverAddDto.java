package rs.fon.bg.ac.rs.marinkovicstefan.dtos.deliveryDriverDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.DeliveryDriver;

public record DeliveryDriverAddDto(String name, String phone, String vehicle, boolean available) {
    public DeliveryDriver toEntity(){
        return DeliveryDriver.builder().name(name).phone(phone).vehicle(vehicle).available(available).build();
    }
}
