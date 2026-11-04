package dev.gustavovs.maisspringsecurity.exception;

public class AppointmentStartTimeNotAvailableException extends RuntimeException {
    public AppointmentStartTimeNotAvailableException() {
        super("Scheduled appointment time is already in use. Try another one.");
    }
}
