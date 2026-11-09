package dev.gustavovs.maisspringsecurity.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PatientPhoneNumberNotFoundException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public PatientPhoneNumberNotFoundException(String phoneNumber) {
        super(phoneNumber + " not found.");
    }
}
