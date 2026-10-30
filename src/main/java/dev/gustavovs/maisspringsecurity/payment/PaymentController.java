package dev.gustavovs.maisspringsecurity.payment;

import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestParam String appointmentId, @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.createPayment(appointmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable String id, @RequestBody PaymentUpdateRequestDTO request) {
        PaymentResponseDTO response = paymentService.updatePayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
