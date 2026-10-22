package dev.gustavovs.maisspringsecurity.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public record AppointmentUpdateRequestDTO (
        Optional<String> title,
        Optional<String> description,
        Optional<LocalDateTime> startAppointment
) {
}
