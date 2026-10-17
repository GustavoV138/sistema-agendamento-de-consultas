package dev.gustavovs.maisspringsecurity.patient.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotEmpty(message = "Name cannot be empty/null.")
        String name,

        @NotEmpty(message = "Phone Number cannot be empty/null.")
        @Size(message = "Phone number must be compound of 11 numbers.", min = 11, max = 11)
        String phoneNumber,

        @NotEmpty(message = "Birthday cannot be empty/null.")
        LocalDate birthday
) {
}
