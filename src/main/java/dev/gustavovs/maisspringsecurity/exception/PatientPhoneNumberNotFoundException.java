package dev.gustavovs.maisspringsecurity.exception;

public class PatientPhoneNumberNotFoundException extends RuntimeException {
    public PatientPhoneNumberNotFoundException(String phoneNumber) {
        super(phoneNumber + " not found.");
    }
}
