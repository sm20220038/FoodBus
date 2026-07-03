package rs.fon.bg.ac.rs.marinkovic_stefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.deliveryDriverDtos.DeliveryDriverAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.deliveryDriverDtos.DeliveryDriverResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.services.DeliveryDriverService;

@RestController
public class DeliveryDriverController {
    private final DeliveryDriverService deliveryDriverService;

    public DeliveryDriverController(DeliveryDriverService deliveryDriverService) {
        this.deliveryDriverService = deliveryDriverService;
    }

    @PostMapping("api/drivers")
    public ResponseEntity<Object> create(@RequestBody DeliveryDriverAddDto driverAdd){
        try {
            DeliveryDriverResponseDto response = deliveryDriverService.create(driverAdd);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("api/drivers/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody DeliveryDriverAddDto driverUpdate){
        try {
            DeliveryDriverResponseDto response = deliveryDriverService.update(id, driverUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("api/drivers/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            deliveryDriverService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Driver deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
