package rs.fon.bg.ac.rs.marinkovic_stefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.orderDtos.OrderAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.orderDtos.OrderResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.orderDtos.OrderStatusUpdateDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.services.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("api/orders")
    public ResponseEntity<Object> create(@RequestBody OrderAddDto orderAdd){
        try {
            OrderResponseDto response = orderService.create(orderAdd);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Create order unsuccessful");
        }
    }

    @PutMapping("api/orders/{id}/status")
    public ResponseEntity<Object> updateStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateDto statusUpdate){
        try {
            OrderResponseDto response = orderService.updateStatus(id, statusUpdate.status());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("api/orders/{id}/cancel")
    public ResponseEntity<Object> cancel(@PathVariable UUID id){
        try {
            OrderResponseDto response = orderService.cancel(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("api/orders/{orderId}/driver/{driverId}")
    public ResponseEntity<Object> assignDriver(@PathVariable UUID orderId, @PathVariable UUID driverId){
        try {
            OrderResponseDto response = orderService.assignDriver(orderId, driverId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("api/orders/{id}/total")
    public ResponseEntity<Object> recalculateTotal(@PathVariable UUID id){
        try {
            BigDecimal total = orderService.recalculateTotal(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(total);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("api/customers/{customerId}/orders")
    public ResponseEntity<Object> orderHistory(@PathVariable UUID customerId){
        try {
            List<OrderResponseDto> response = orderService.findAllByCustomer(customerId);
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
