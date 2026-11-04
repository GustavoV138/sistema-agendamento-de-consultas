package dev.gustavovs.maisspringsecurity.exception;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(String id) {
        super(id + " not found.");
    }
}
