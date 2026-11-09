package dev.gustavovs.maisspringsecurity.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentNotFoundException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public PaymentNotFoundException(String id) {
        super(id + " not found.");
    }
}
