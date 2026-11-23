package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.exception.exceptions.PatientNotFoundException;
import dev.gustavovs.maisspringsecurity.exception.exceptions.PatientPhoneNumberNotFoundException;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.response.PatientResponseDTO;
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
import static org.mockito.Mockito.doNothing;
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

    private PatientUpdateRequestDTO createValidUpdateRequest() {
        return new PatientUpdateRequestDTO(
                Optional.of("Gustavo updated"),
                Optional.of("81987654321"),
                Optional.of(LocalDate.parse("2002-07-10"))
        );
    }

    private PatientResponseDTO createValidResponse() {
        return new PatientResponseDTO("ABC123d", "Gustavo teste", "81912345678", LocalDate.parse("2001-08-09"));
    }

    private Patient createValidPatient() {
        Patient patient = new Patient();
        patient.setId("ABC123d");
        patient.setName("Gustavo teste");
        patient.setPhoneNumber("81912345678");
        patient.setBirthday(LocalDate.parse("2001-08-09"));

        return patient;
    }

    private Patient createValidUpdatedPatient() {
        Patient patient = new Patient();
        patient.setId("ABC123d");
        patient.setName("Gustavo updated");
        patient.setPhoneNumber("81987654321");
        patient.setBirthday(LocalDate.parse("2002-07-10"));

        return patient;
    }

    @Test
    public void createPatient_Success() {
        PatientRequestDTO request = createValidRequest();

        when(patientRepository.findByPhoneNumber("81912345678"))
                .thenReturn(List.of());

        when(patientRepository.save(Mockito.any(Patient.class)))
                .thenReturn(createValidPatient());

        Patient patientCreated = patientService.createPatient(request);

        assertNotNull(patientCreated);
        assertEquals(patientCreated.getName(), request.name());
        assertEquals(patientCreated.getPhoneNumber(), request.phoneNumber());
        assertEquals(patientCreated.getBirthday(), request.birthday());
    }

    @Test
    public void createPatient_When_Patient_Exists() {
        PatientRequestDTO request = createValidRequest();

        when(patientRepository.findByPhoneNumber("81912345678"))
                .thenReturn(List.of(createValidPatient()));

        Patient patientCreated = patientService.createPatient(request);

        assertNotNull(patientCreated);
        assertEquals(patientCreated.getName(), request.name());
        assertEquals(patientCreated.getPhoneNumber(), request.phoneNumber());
        assertEquals(patientCreated.getBirthday(), request.birthday());
    }

    @Test
    public void findPatientByPhoneNumber_Success() {
        when(patientRepository.findByPhoneNumber("81912345678"))
                .thenReturn(List.of(createValidPatient()));

        List<PatientResponseDTO> patients = patientService.findPatientByPhoneNumber("81912345678");

        assertNotNull(patients);
        assertEquals(1, patients.size());
        assertEquals(createValidResponse(), patients.getFirst());
    }

    @Test
    public void findPatientByPhoneNumber_Throws_PatientPhoneNumberNotFoundException() {
        when(patientRepository.findByPhoneNumber("81912345678"))
                .thenReturn(List.of());

        assertThrowsExactly(PatientPhoneNumberNotFoundException.class, () -> patientService.findPatientByPhoneNumber("81912345678"));
    }

    @Test
    public void updatePatient_Success() {
        when(patientRepository.findById("ABC123d"))
                .thenReturn(Optional.of(createValidPatient()));

        when(patientRepository.save(Mockito.any(Patient.class)))
                .thenReturn(createValidUpdatedPatient());

        PatientResponseDTO updatedPatient = patientService.updatePatient("ABC123d", createValidUpdateRequest());

        assertNotNull(updatedPatient);
        assertEquals("ABC123d", updatedPatient.id());
        assertEquals("Gustavo updated", updatedPatient.name());
        assertEquals("81987654321", updatedPatient.phoneNumber());
        assertEquals(LocalDate.parse("2002-07-10"), updatedPatient.birthday());

    }

    @Test
    public void updatePatient_Throws_PatientNotFoundException() {
        when(patientRepository.findById("ABC123d"))
                .thenReturn(Optional.empty());

        assertThrowsExactly(PatientNotFoundException.class, () -> patientService.updatePatient("ABC123d", createValidUpdateRequest()));
    }

    @Test
    public void deletePatient_Success() {
        when(patientRepository.findById("ABC123d"))
                .thenReturn(Optional.of(createValidPatient()));

        doNothing().when(patientRepository).delete(createValidPatient());

        assertDoesNotThrow(() -> patientService.deletePatient("ABC123d"));
    }
}