package dev.gustavovs.maisspringsecurity.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    List<Patient> findByPhoneNumber(String phoneNumber);

}
