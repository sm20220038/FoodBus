package rs.fon.bg.ac.rs.marinkovicstefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.customerdtos.CustomerAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.customerdtos.CustomerResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.services.CustomerService;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("api/customers")
    public ResponseEntity<Object> create(@RequestBody CustomerAddDto customerAdd){
        try {
            CustomerResponseDto response = customerService.create(customerAdd);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("api/customers/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody CustomerAddDto customerUpdate){
        try {
            CustomerResponseDto response = customerService.update(id, customerUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("api/customers/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            customerService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Customer deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
