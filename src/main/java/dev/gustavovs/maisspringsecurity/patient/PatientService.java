package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.exception.exceptions.PatientNotFoundException;
import dev.gustavovs.maisspringsecurity.exception.exceptions.PatientPhoneNumberNotFoundException;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.patient.dto.response.PatientResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(PatientRequestDTO request) {

        Optional<Patient> patientFoundAfterVerify = verifyBeforeCreatePatient(request);

        if(patientFoundAfterVerify.isPresent()) {
            Patient patientFound = patientFoundAfterVerify.get();
            return patientFound;
        }

        Patient patient = new Patient();

        patient.setName(request.name());
        patient.setPhoneNumber(request.phoneNumber());
        patient.setBirthday(request.birthday());

        return patientRepository.save(patient);
    }

    public List<PatientResponseDTO> findPatientByPhoneNumber(String phoneNumber) {

        List<Patient> patients = patientRepository.findByPhoneNumber(phoneNumber);
        if (patients.isEmpty())
            throw new PatientPhoneNumberNotFoundException(phoneNumber);

        return patients.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public PatientResponseDTO updatePatient(String id, PatientUpdateRequestDTO request) {

        Patient patient = internalFindById(id);

        if(request.name().isPresent()) patient.setName(request.name().get());
        if(request.phoneNumber().isPresent()) patient.setPhoneNumber(request.phoneNumber().get());
        if(request.birthday().isPresent()) patient.setBirthday(request.birthday().get());

        patientRepository.save(patient);

        return toResponseDTO(patient);
    }

    public void deletePatient(String id) {
        Patient patient = internalFindById(id);
        patientRepository.delete(patient);
    }


    private Optional<Patient> verifyBeforeCreatePatient(PatientRequestDTO request) {

        List<Patient> byPhoneNumber = patientRepository.findByPhoneNumber(request.phoneNumber());

        boolean patientIsFound = byPhoneNumber.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.name()) && p.getBirthday().equals(request.birthday()));

        if(patientIsFound) {
            return byPhoneNumber.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(request.name()) && p.getBirthday().equals(request.birthday()))
                    .findFirst();
        }

        return Optional.empty();
    }

    private Patient internalFindById(String id) {
        Optional<Patient> patientById = patientRepository.findById(id);
        if(patientById.isEmpty()) throw new PatientNotFoundException(id);
        return patientById.get();
    }

    private PatientResponseDTO toResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getPhoneNumber(),
                patient.getBirthday()
        );
    }
}
