package dev.gustavovs.maisspringsecurity.patient.dto.request;

import java.time.LocalDate;
import java.util.Optional;

public record PatientUpdateRequestDTO(
        Optional<String> name,
        Optional<String> phoneNumber,
        Optional<LocalDate> birthday) {
}
