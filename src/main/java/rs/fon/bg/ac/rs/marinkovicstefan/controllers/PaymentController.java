package rs.fon.bg.ac.rs.marinkovicstefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.paymentDtos.PaymentAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.paymentDtos.PaymentResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.services.PaymentService;

@RestController
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("api/payments")
    public ResponseEntity<Object> pay(@RequestBody PaymentAddDto paymentAdd){
        try {
            PaymentResponseDto response = paymentService.pay(paymentAdd);
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
