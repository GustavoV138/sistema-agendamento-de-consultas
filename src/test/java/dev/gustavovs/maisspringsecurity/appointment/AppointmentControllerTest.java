package dev.gustavovs.maisspringsecurity.appointment;

import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.response.AppointmentResponseDTO;
import dev.gustavovs.maisspringsecurity.exception.GlobalExceptionHandler;
import dev.gustavovs.maisspringsecurity.exception.exceptions.AppointmentNotFoundException;
import dev.gustavovs.maisspringsecurity.exception.exceptions.AppointmentStartTimeNotAvailableException;
import dev.gustavovs.maisspringsecurity.payment.PaymentType;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AppointmentController(appointmentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private AppointmentResponseDTO createValidResponse() {
        return new AppointmentResponseDTO(
                "app_id",
                "Appointment Title",
                "Appointment desc",
                LocalDateTime.of(2026, 11, 19, 14, 32),
                LocalDateTime.of(2026, 11, 19, 15, 5),
                "ACTIVE",
                "Patient name",
                "012345"
        );
    }

    // Endpoint example: "/appointment/by/{value}/..."
    private void verifyByDateEndpoints(String endpoint) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/appointment/by/" + endpoint))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("app_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Appointment Title"));
    }

    @Test
    public void createAppointment_Success_Returns201() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.createAppointment(any(AppointmentRequestDTO.class)))
                .thenReturn(response);

        String payload = """
                {
                    "title": "Appointment Title",
                    "description": "Appointment desc",
                    "startAppointment": "2026-11-19T14:32:00",
                    "patientName": "Patient name",
                    "patientPhoneNumber": "012345",
                    "patientBirthday": "2001-08-08"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Appointment Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.patientName").value("Patient name"));

        verify(appointmentService).createAppointment(any(AppointmentRequestDTO.class));
    }

    @Test
    public void createAppointment_InvalidJsonArguments_Returns400() throws Exception {
        String payload = """
                {
                    "title": "",
                    "description": "",
                    "startAppointment": "",
                    "patientName": "",
                    "patientPhoneNumber": "",
                    "patientBirthday": ""
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("One or more invalid fields."));
    }

    @Test
    public void createAppointment_Appointment_StartTime_NotAvailable_Returns409() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentRequestDTO.class)))
                .thenThrow(new AppointmentStartTimeNotAvailableException());

        String payload = """
                {
                    "title": "Appointment Title",
                    "description": "Appointment desc",
                    "startAppointment": "2026-11-19T14:32:00",
                    "patientName": "Patient name",
                    "patientPhoneNumber": "012345",
                    "patientBirthday": "2001-08-08"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Start time not available."));
    }

    @Test
    public void findAppointmentsBySituation_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.findAppointmentsBySituation(any(AppointmentSituation.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/appointment")
                        .param("situation", "ACTIVE"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("app_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Appointment Title"));

        verify(appointmentService).findAppointmentsBySituation(any(AppointmentSituation.class));
    }

    @Test
    public void findAppointmentByDate_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.findAppointmentByDate(any(LocalDate.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/appointment/by/2026-10-10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("app_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Appointment Title"));

        verify(appointmentService).findAppointmentByDate(any(LocalDate.class));
    }

    @Test
    public void findAppointmentsByDay_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.findAppointmentsOnDay(anyInt()))
                .thenReturn(List.of(response));

        verifyByDateEndpoints("/day/10");

        verify(appointmentService).findAppointmentsOnDay(anyInt());
    }

    @Test
    public void findAppointmentsByMonth_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.findAppointmentsOnMonth(anyInt()))
                .thenReturn(List.of(response));

        verifyByDateEndpoints("/month/10");

        verify(appointmentService).findAppointmentsOnMonth(anyInt());
    }

    @Test
    public void findAppointmentsByYear_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.findAppointmentsOnYear(anyInt()))
                .thenReturn(List.of(response));

        verifyByDateEndpoints("/year/2026");

        verify(appointmentService).findAppointmentsOnYear(anyInt());
    }

    @Test
    public void updateOnlyAppointmentInfos_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = createValidResponse();

        when(appointmentService.updateOnlyAppointmentInfos(anyString(), any(AppointmentUpdateRequestDTO.class)))
                .thenReturn(response);

        String payload = """
                {
                 "title": "Appointment Title",
                 "description": "Appointment desc"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/appointment/app_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("app_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Appointment Title"));

        verify(appointmentService).updateOnlyAppointmentInfos(anyString(), any(AppointmentUpdateRequestDTO.class));
    }

    @Test
    public void updateOnlyAppointmentInfos_Appointment_NotFound_Returns404() throws Exception {
        when(appointmentService.updateOnlyAppointmentInfos(anyString(), any(AppointmentUpdateRequestDTO.class)))
                .thenThrow(new AppointmentNotFoundException("app_id"));

        String payload = """
                {
                 "title": "Appointment Title",
                 "description": "Appointment desc"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/appointment/app_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Appointment not found."));
    }

    @Test
    public void deleteAppointment_Success_Returns204() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/appointment/app_id"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(appointmentService).deleteAppointment(anyString());
    }

    @Test
    public void markAppointmentAsCompleted_Success_Returns200() throws Exception {
        AppointmentResponseDTO response = new AppointmentResponseDTO(
                "app_id",
                "Appointment Title",
                "Appointment desc",
                LocalDateTime.of(2026, 11, 19, 14, 32),
                LocalDateTime.of(2026, 11, 19, 15, 5),
                "COMPLETED",
                "Patient name",
                "012345"
        );

        when(appointmentService.markAppointmentAsCompleted(anyString()))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/appointment/complete/app_id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("app_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.situation").value("COMPLETED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endAppointment").value(LocalDateTime.of(2026, 11, 19, 15, 5)
                        .format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));

        verify(appointmentService).markAppointmentAsCompleted(anyString());
    }

    @Test
    public void findAllAppointmentPayments_Success_Returns200() throws Exception{
        PaymentResponseDTO response = new PaymentResponseDTO("pay_id", BigDecimal.valueOf(150), PaymentType.PIX, true);

        when(appointmentService.findAllAppointmentPayments(anyString()))
                .thenReturn(List.of(response));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/appointment/app_id/payments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("pay_id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].value").value(BigDecimal.valueOf(150)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].isDone").value(true));

        verify(appointmentService).findAllAppointmentPayments(anyString());
    }

}