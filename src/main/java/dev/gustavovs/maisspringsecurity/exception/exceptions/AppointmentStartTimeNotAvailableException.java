package dev.gustavovs.maisspringsecurity.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppointmentStartTimeNotAvailableException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public AppointmentStartTimeNotAvailableException() {
        super("Scheduled appointment time is already in use. Try another one.");
    }
}
