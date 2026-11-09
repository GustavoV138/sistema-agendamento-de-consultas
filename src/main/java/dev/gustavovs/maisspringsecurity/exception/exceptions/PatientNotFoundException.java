package dev.gustavovs.maisspringsecurity.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PatientNotFoundException extends RuntimeException{

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public PatientNotFoundException(String id) {
        super(id + " not found.");
    }
}
