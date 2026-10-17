package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.response.PatientResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(PatientRequestDTO request) {

        if(verifyBeforeCreate(request)) {
            Patient patientFound = findPatientByPhoneNumber(request.phoneNumber()).stream()
                    .filter(p -> p.getName().equalsIgnoreCase(request.name()))
                    .filter(p -> p.getBirthday().equals(request.birthday()))
                    .findFirst()
                    .get();

            return patientFound;
        }

        Patient patient = new Patient();

        patient.setName(request.name());
        patient.setPhoneNumber(request.phoneNumber());
        patient.setBirthday(request.birthday());

        return patientRepository.save(patient);
    }

    private boolean verifyBeforeCreate(PatientRequestDTO request) {

        List<Patient> byPhoneNumber = findPatientByPhoneNumber(request.phoneNumber());

        return byPhoneNumber.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.name()) && p.getBirthday().equals(request.birthday()));
    }

    public List<Patient> findPatientByPhoneNumber(String phoneNumber) {

        List<Patient> patients = patientRepository.findByPhoneNumber(phoneNumber);
        if (patients.isEmpty())
            throw new RuntimeException(phoneNumber + " not found."); // Should throw PatientNotFoundException

        return patients;
    }

    @Transactional
    public PatientResponseDTO updatePatient(String id, PatientUpdateRequestDTO request) {

        Patient patient = internalFindById(id);

        if(request.name().isPresent()) patient.setName(request.name().get());
        if(request.phoneNumber().isPresent()) patient.setPhoneNumber(request.phoneNumber().get());
        if(request.birthday().isPresent()) patient.setBirthday(request.birthday().get());

        patientRepository.save(patient);

        return new PatientResponseDTO(
                patient.getName(),
                patient.getPhoneNumber(),
                patient.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void deletePatient(String id) {

        Patient patient = internalFindById(id);
        patientRepository.delete(patient);
    }

    private Patient internalFindById(String id) {
        Optional<Patient> patientById = patientRepository.findById(id);
        if(patientById.isEmpty()) throw new RuntimeException(id + " not found.");
        return patientById.get();
    }
}
