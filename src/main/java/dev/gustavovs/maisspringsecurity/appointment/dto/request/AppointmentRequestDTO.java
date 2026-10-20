package dev.gustavovs.maisspringsecurity.appointment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public record AppointmentRequestDTO(
        @NotEmpty(message = "Title cannot be empty/null.")
        @Size(max = 200)
        String title,

        @Size(max = 1500)
        Optional<String> description,

        @NotEmpty(message = "Appointment start date cannot be empty/null.")
        LocalDateTime startAppointment,

        @NotEmpty(message = "Patient name cannot be empty/null.")
        String patientName,

        @NotEmpty(message = "Patient phone number cannot be empty/null.")
        String patientPhoneNumber,

        @NotEmpty(message = "Patient birthday cannot be empty/null.")
        LocalDate patientBirthday
) {
}
