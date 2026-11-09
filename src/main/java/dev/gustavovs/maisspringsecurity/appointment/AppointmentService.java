package dev.gustavovs.maisspringsecurity.appointment;

import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.response.AppointmentResponseDTO;
import dev.gustavovs.maisspringsecurity.exception.exceptions.AppointmentNotFoundException;
import dev.gustavovs.maisspringsecurity.exception.exceptions.AppointmentStartTimeNotAvailableException;
import dev.gustavovs.maisspringsecurity.patient.Patient;
import dev.gustavovs.maisspringsecurity.patient.PatientService;
import dev.gustavovs.maisspringsecurity.patient.dto.request.PatientRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.Payment;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;

    public AppointmentService (AppointmentRepository appointmentRepository, PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
    }

    public AppointmentResponseDTO createAppointment (AppointmentRequestDTO request) {

        verifyDateAvailability(request.startAppointment());

        Patient patient = patientService.createPatient(new PatientRequestDTO(
                request.patientName(),
                request.patientPhoneNumber(),
                request.patientBirthday())
        );

        Appointment appointment = new Appointment();

        appointment.setTitle(request.title());
        if(request.description().isPresent()) appointment.setDescription(request.description().get());
        appointment.setStartAppointment(request.startAppointment());
        appointment.setPatient(patient);

        Appointment saved = appointmentRepository.save(appointment);

        return toAppointmentResponse(saved);
    }

    //>> Start of Filters

    // By Situation
    public List<AppointmentResponseDTO> findAppointmentsBySituation(AppointmentSituation situation) {
        List<Appointment> bySituation = appointmentRepository.findBySituation(situation);

        return bySituation.stream()
                .map(this::toAppointmentResponse)
                .toList();
    }

    // By time period
    public List<AppointmentResponseDTO> findAppointmentsOnDay(int day) {
        List<Appointment> appointmentsOnDay = appointmentRepository.findAppointmentsOnDay(day);

        return appointmentsOnDay.stream()
                .map(this::toAppointmentResponse)
                .toList();
    }

    public List<AppointmentResponseDTO> findAppointmentsOnMonth(int month) {
        List<Appointment> appointmentsOnMonth = appointmentRepository.findAppointmentsOnMonth(month);

        return appointmentsOnMonth.stream()
                .map(this::toAppointmentResponse)
                .toList();
    }

    public List<AppointmentResponseDTO> findAppointmentsOnYear(int year) {
        List<Appointment> appointmentsOnYear = appointmentRepository.findAppointmentsOnYear(year);
        return appointmentsOnYear.stream()
                .map(this::toAppointmentResponse)
                .toList();
    }
    //<< End of Filters

    public AppointmentResponseDTO findAppointmentById(String id) {
        return toAppointmentResponse(findByIdInternal(id));
    }

    @Transactional
    public AppointmentResponseDTO updateOnlyAppointmentInfos(String id, AppointmentUpdateRequestDTO request) {
        Appointment appointmentFound = findByIdInternal(id);

        if (request.title().isPresent()) appointmentFound.setTitle(request.title().get());
        if (request.description().isPresent()) appointmentFound.setDescription(request.description().get());

        if (request.startAppointment().isPresent()){
            verifyDateAvailability(request.startAppointment().get());// Change situation
            appointmentFound.setStartAppointment(request.startAppointment().get());
            appointmentFound.setSituation(AppointmentSituation.RESCHEDULED);
        }

        appointmentRepository.save(appointmentFound);
        return toAppointmentResponse(appointmentFound);
    }

    public void deleteAppointment (String id) {
        Appointment byId = findByIdInternal(id);
        appointmentRepository.delete(byId);
    }

    public void markAppointmentAsCompleted(String id) {
        Appointment byId = findByIdInternal(id);
        byId.setSituation(AppointmentSituation.COMPLETED);

        if (byId.getEndAppointment() == null) byId.setEndAppointment(LocalDateTime.now(ZoneId.of("-03:00")));
        appointmentRepository.save(byId);
    }

    public List<PaymentResponseDTO> findAllAppointmentPayments(String id) {
        Appointment byId = findByIdInternal(id);

        List<Payment> payments = byId.getPayments();

        return payments.stream().map(
                p -> (new PaymentResponseDTO(
                        p.getId(),
                        p.getValue(),
                        p.getPaymentType(),
                        p.getIsDone()))
                )
                .toList();
    }

    private void verifyDateAvailability(LocalDateTime startAppointment) {
        Optional<Appointment> byStartAppointment = appointmentRepository.findByStartAppointment(startAppointment.minusHours(1), startAppointment);

        if(byStartAppointment.isPresent()) throw new AppointmentStartTimeNotAvailableException();
    }

    private Appointment findByIdInternal(String id) {
        Optional<Appointment> byId = appointmentRepository.findById(id);

        if(byId.isPresent()) return byId.get();
        else throw new AppointmentNotFoundException(id);
    }

    private AppointmentResponseDTO toAppointmentResponse(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getStartAppointment(),
                appointment.getEndAppointment(),
                appointment.getSituation().toString(),
                appointment.getPatient().getName(),
                appointment.getPatient().getPhoneNumber()
        );
    }

}
