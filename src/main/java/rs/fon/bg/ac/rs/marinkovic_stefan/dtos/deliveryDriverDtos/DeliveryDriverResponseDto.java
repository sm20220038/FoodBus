package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.deliveryDriverDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.DeliveryDriver;

public record DeliveryDriverResponseDto(Long id, String name, String phone, String vehicle, boolean available) {

    public static DeliveryDriverResponseDto fromEntity(DeliveryDriver driver){
        return new DeliveryDriverResponseDto(driver.getId(), driver.getName(), driver.getPhone(),
                driver.getVehicle(), driver.isAvailable());
    }
}
