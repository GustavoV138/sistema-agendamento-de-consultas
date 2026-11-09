package dev.gustavovs.maisspringsecurity.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailAlreadyInUseException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public EmailAlreadyInUseException() {
        super("Email already in use.");
    }
}
