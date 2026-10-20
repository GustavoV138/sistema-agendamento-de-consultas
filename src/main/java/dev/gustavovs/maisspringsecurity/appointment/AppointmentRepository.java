package dev.gustavovs.maisspringsecurity.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    @Query("SELECT a FROM appointment a WHERE extract(day FROM a.start_appointment) = :day AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    List<Appointment> findAppointmentsOnDay(@Param("day") int day);

    @Query("SELECT a FROM appointment a WHERE extract(month FROM a.start_appointment) = :month AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    List<Appointment> findAppointmentsOnMonth(@Param("month") int month);

    @Query("SELECT a FROM appointment a WHERE extract(year FROM a.start_appointment) = :year AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    List<Appointment> findAppointmentsOnYear(@Param("year") int year);

    @Query("SELECT a FROM appointment a WHERE a.start_appointment BETWEEN (:initialTime AND :scheduledTime)  AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    Optional<Appointment> findByStartAppointment(@Param("initialTime") LocalDateTime initialTime, @Param("scheduledTime") LocalDateTime scheduledTime);

    List<Appointment> findBySituation(AppointmentSituation situation);
}
