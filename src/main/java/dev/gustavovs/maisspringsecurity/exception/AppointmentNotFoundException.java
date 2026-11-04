package dev.gustavovs.maisspringsecurity.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String id) {
        super(id + " not found.");
    }
}
