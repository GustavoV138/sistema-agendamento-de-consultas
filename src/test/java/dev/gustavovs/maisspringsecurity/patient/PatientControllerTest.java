package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.exception.GlobalExceptionHandler;
import dev.gustavovs.maisspringsecurity.exception.exceptions.PatientNotFoundException;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.response.PatientResponseDTO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setTup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PatientController(patientService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void findPatientByPhoneNumber_Success_Returns200() throws Exception {
        PatientResponseDTO response = new PatientResponseDTO("patient_id", "Patient Title", "12345", LocalDate.parse("2001-08-09"));

        when(patientService.findPatientByPhoneNumber(any(String.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patient/12345"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(response.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(response.name()));

        verify(patientService).findPatientByPhoneNumber("12345");
    }

    @Test
    public void updatePatient_Success_Returns200() throws Exception {
        PatientResponseDTO response = new PatientResponseDTO("patient_id", "Patient Title Updated", "54321", LocalDate.parse("2001-08-09"));

        when(patientService.updatePatient(any(String.class), any(PatientUpdateRequestDTO.class)))
                .thenReturn(response);

        String payload = """
                {
                    "name": "Patient Title Updated",
                    "phoneNumber": "54321"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/patient/patient_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(response.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(response.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value(response.phoneNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(response.birthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        verify(patientService).updatePatient(any(String.class), any(PatientUpdateRequestDTO.class));
    }

    @Test
    public void updatePatient_NotFindId_Returns404() throws Exception {
        when(patientService.updatePatient(any(String.class), any(PatientUpdateRequestDTO.class)))
                .thenThrow(new PatientNotFoundException("patient_id"));

        String payload = """
                {
                    "name": "Patient Title Updated",
                    "phoneNumber": "54321"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/patient/patient_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Patient not found."));
    }

    @Test
    public void deletePatient_Success_Returns204() throws Exception {
        doNothing().when(patientService).deletePatient(any(String.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/patient/patient_id"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(patientService).deletePatient(any(String.class));
    }

}