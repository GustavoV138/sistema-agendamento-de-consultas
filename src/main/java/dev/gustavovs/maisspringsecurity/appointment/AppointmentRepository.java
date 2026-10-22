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

    @Query("SELECT a FROM Appointment a WHERE extract(day FROM a.startAppointment) = :day AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    List<Appointment> findAppointmentsOnDay(@Param("day") int day);

    @Query("SELECT a FROM Appointment a WHERE extract(month FROM a.startAppointment) = :month AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    List<Appointment> findAppointmentsOnMonth(@Param("month") int month);

    @Query("SELECT a FROM Appointment a WHERE extract(year FROM a.startAppointment) = :year")
    List<Appointment> findAppointmentsOnYear(@Param("year") int year);

    @Query("SELECT a FROM Appointment a WHERE a.startAppointment BETWEEN :initialTime AND :scheduledTime  AND (a.situation = 'ACTIVE' OR a.situation = 'RESCHEDULED')")
    Optional<Appointment> findByStartAppointment(@Param("initialTime") LocalDateTime initialTime, @Param("scheduledTime") LocalDateTime scheduledTime);

    List<Appointment> findBySituation(AppointmentSituation situation);
}
