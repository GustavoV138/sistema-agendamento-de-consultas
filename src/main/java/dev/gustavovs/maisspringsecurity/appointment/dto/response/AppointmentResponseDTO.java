package dev.gustavovs.maisspringsecurity.appointment.dto.response;

public record AppointmentResponseDTO(
        String title,
        String description,
        String startAppointment,
        String endAppointment,
        String situation,
        String patientName,
        String patientPhoneNumber
) {
}
