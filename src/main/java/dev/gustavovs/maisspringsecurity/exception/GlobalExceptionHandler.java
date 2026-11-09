package dev.gustavovs.maisspringsecurity.exception;

import dev.gustavovs.maisspringsecurity.exception.exceptions.*;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAppointmentNotFoundException(AppointmentNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Appointment not found.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(AppointmentStartTimeNotAvailableException.class)
    public ResponseEntity<ProblemDetail> handleAppointmentStartTimeNotAvailableException(AppointmentStartTimeNotAvailableException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Start time not available.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePatientNotFoundException(PatientNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Patient not found.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(PatientPhoneNumberNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePatientPhoneNumberNotFoundException(PatientPhoneNumberNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Patient phone number not found.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePaymentNotFoundException(PaymentNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Payment not found.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUseException(EmailAlreadyInUseException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
        problemDetail.setTitle("Email already in use.");
        return ResponseEntity.status(exception.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        Map<String, String> errors = new HashMap<>();

        fieldErrors.forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getMessage());
        problemDetail.setTitle("One or more invalid fields.");
        problemDetail.setProperty("Invalid fields", errors);

        return ResponseEntity.status(exception.getStatusCode()).body(problemDetail);
    }

}
