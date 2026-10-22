package dev.gustavovs.maisspringsecurity.appointment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        String id,
        String title,
        String description,

        @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
        LocalDateTime startAppointment,
        @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
        LocalDateTime endAppointment,

        String situation,
        String patientName,
        String patientPhoneNumber
) {
}
