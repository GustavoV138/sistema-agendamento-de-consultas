package dev.gustavovs.maisspringsecurity.patient.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PatientResponseDTO(
        String id,
        String name,
        String phoneNumber,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthday) {
}
