package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientRequestDTO createValidRequest() {
        return new PatientRequestDTO("Gustavo teste", "81912345678", LocalDate.parse("2001-08-09"));
    }

    @Test
    public void create_Patient_Success() {
        PatientRequestDTO request = createValidRequest();

        when(patientRepository.findByPhoneNumber("81912345678"))
                .thenReturn(List.of());

        Patient patientCreated = patientService.createPatient(request);
    }

}