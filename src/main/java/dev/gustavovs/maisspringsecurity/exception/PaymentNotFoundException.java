package dev.gustavovs.maisspringsecurity.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String id) {
        super(id + " not found.");
    }
}
