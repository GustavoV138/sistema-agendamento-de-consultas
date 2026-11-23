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
import dev.gustavovs.maisspringsecurity.payment.PaymentType;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient createValidPatient() {
        Patient patient = new Patient();
        patient.setId("ABC123d");
        patient.setName("Gustavo Test");
        patient.setPhoneNumber("81912345678");
        patient.setBirthday(LocalDate.parse("2001-08-09"));

        return patient;
    }



    private AppointmentRequestDTO createValidRequestAppointment() {
        return new AppointmentRequestDTO(
                "Appointment Test",
                Optional.empty(),
                LocalDateTime.of(2026, 9, 11, 14, 30),
                "Gustavo Test",
                "81912345678",
                LocalDate.parse("2001-08-09")
        );
    }

    private Appointment createValidAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId("ZXC456v");
        appointment.setTitle("Appointment Test");
        appointment.setStartAppointment(LocalDateTime.of(2026, 9, 11, 14, 30));
        appointment.setSituation(AppointmentSituation.ACTIVE);
        appointment.setPatient(createValidPatient());

        return appointment;
    }

    @Test
    public void createAppointment_Success() {
        PatientRequestDTO requestDTO = new PatientRequestDTO("Gustavo Test", "81912345678", LocalDate.parse("2001-08-09"));
        Patient patient = createValidPatient();

        when(appointmentRepository.findByStartAppointment(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        when(patientService.createPatient(requestDTO))
                .thenReturn(patient);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(createValidAppointment());

        AppointmentResponseDTO appointmentCreated = appointmentService.createAppointment(createValidRequestAppointment());

        assertNotNull(appointmentCreated);
        assertEquals("ZXC456v", appointmentCreated.id());
        assertEquals("Appointment Test", appointmentCreated.title());
        assertEquals(LocalDateTime.of(2026, 9, 11, 14, 30), appointmentCreated.startAppointment());
        assertEquals("ACTIVE", appointmentCreated.situation());
        assertEquals(patient.getName(), appointmentCreated.patientName());
        assertEquals(patient.getPhoneNumber(), appointmentCreated.patientPhoneNumber());

    }

    @Test
    public void createAppointment_Throws_AppointmentStartTimeNotAvailableException() {

        when(appointmentRepository.findByStartAppointment(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(createValidAppointment()));

        assertThrowsExactly(AppointmentStartTimeNotAvailableException.class, () -> appointmentService.createAppointment(createValidRequestAppointment()));

    }

    @Test
    public void findAppointmentsBySituation_Success() {
        when(appointmentRepository.findBySituation(AppointmentSituation.ACTIVE))
                .thenReturn(List.of(createValidAppointment()));

        List<AppointmentResponseDTO> appointments = appointmentService.findAppointmentsBySituation(AppointmentSituation.ACTIVE);

        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        assertEquals("ZXC456v", appointments.getFirst().id());
        assertEquals("Appointment Test", appointments.getFirst().title());
    }

    @Test
    public void findAppointmentsBySituation_Returns_Empty_List() {
        when(appointmentRepository.findBySituation(AppointmentSituation.ACTIVE))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> appointments = appointmentService.findAppointmentsBySituation(AppointmentSituation.ACTIVE);

        assertNotNull(appointments);
        assertEquals(0, appointments.size());
    }

    @Test
    public void findAppointmentByDate_Success() {
        when(appointmentRepository.findByDate(any(LocalDate.class)))
                .thenReturn(List.of(createValidAppointment()));

        List<AppointmentResponseDTO> appointmentsByDate = appointmentService.findAppointmentByDate(LocalDate.parse("2026-09-11"));

        assertNotNull(appointmentsByDate);
        assertEquals(1, appointmentsByDate.size());
        assertEquals("ZXC456v", appointmentsByDate.getFirst().id());
        assertEquals("Appointment Test", appointmentsByDate.getFirst().title());
    }

    @Test
    public void findAppointmentByDate_Returns_Empty_List() {
        when(appointmentRepository.findByDate(any(LocalDate.class)))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> appointmentsByDate = appointmentService.findAppointmentByDate(LocalDate.parse("2026-09-11"));

        assertNotNull(appointmentsByDate);
        assertEquals(0, appointmentsByDate.size());
    }

    @Test
    public void findAppointmentsOnDay_Success() {
        when(appointmentRepository.findAppointmentsOnDay(11))
                .thenReturn(List.of(createValidAppointment()));

        List<AppointmentResponseDTO> appointmentsOnDay = appointmentService.findAppointmentsOnDay(11);

        assertNotNull(appointmentsOnDay);
        assertEquals(1, appointmentsOnDay.size());
        assertEquals("ZXC456v", appointmentsOnDay.getFirst().id());
    }

    @Test
    public void findAppointmentsOnDay_Returns_Empty_List() {
        when(appointmentRepository.findAppointmentsOnDay(11))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> appointmentsOnDay = appointmentService.findAppointmentsOnDay(11);

        assertNotNull(appointmentsOnDay);
        assertEquals(0, appointmentsOnDay.size());
    }

    @Test
    public void findAppointmentsOnMonth_Success() {
        when(appointmentRepository.findAppointmentsOnMonth(9))
                .thenReturn(List.of(createValidAppointment()));

        List<AppointmentResponseDTO> appointmentsOnMonth = appointmentService.findAppointmentsOnMonth(9);

        assertNotNull(appointmentsOnMonth);
        assertEquals(1, appointmentsOnMonth.size());
        assertEquals("ZXC456v", appointmentsOnMonth.getFirst().id());
    }

    @Test
    public void findAppointmentsOnMonth_Returns_Empty_List() {
        when(appointmentRepository.findAppointmentsOnMonth(9))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> appointmentsOnMonth = appointmentService.findAppointmentsOnMonth(9);

        assertNotNull(appointmentsOnMonth);
        assertEquals(0, appointmentsOnMonth.size());
    }

    @Test
    public void findAppointmentsOnYear_Success() {
        when(appointmentRepository.findAppointmentsOnYear(2026))
                .thenReturn(List.of(createValidAppointment()));

        List<AppointmentResponseDTO> appointmentsOnYear = appointmentService.findAppointmentsOnYear(2026);

        assertNotNull(appointmentsOnYear);
        assertEquals(1, appointmentsOnYear.size());
        assertEquals("ZXC456v", appointmentsOnYear.getFirst().id());
    }

    @Test
    public void findAppointmentsOnYear_Returns_Empty_List() {
        when(appointmentRepository.findAppointmentsOnYear(2026))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> appointmentsOnYear = appointmentService.findAppointmentsOnYear(2026);

        assertNotNull(appointmentsOnYear);
        assertEquals(0, appointmentsOnYear.size());
    }

    @Test
    public void updateOnlyAppointmentInfos_Success() {

        AppointmentUpdateRequestDTO request = new AppointmentUpdateRequestDTO(
                Optional.of("Appointment updated"),
                Optional.empty(),
                Optional.of(LocalDateTime.now())
        );

        Appointment appointment = createValidAppointment();

        when(appointmentRepository.findById("ZXC456v"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.findByStartAppointment(any(), any()))
                .thenReturn(Optional.empty());

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        AppointmentResponseDTO response = appointmentService.updateOnlyAppointmentInfos("ZXC456v", request);

        assertNotNull(response);
        assertEquals("ZXC456v", response.id());
        assertEquals("Appointment updated", response.title());
        assertNull(response.endAppointment());
        assertEquals("RESCHEDULED", response.situation());
    }

    @Test
    public void updateOnlyAppointmentInfos_Throws_AppointmentNotFoundException() {
        AppointmentUpdateRequestDTO request = new AppointmentUpdateRequestDTO(
                Optional.of("Appointment updated"),
                Optional.empty(),
                Optional.of(LocalDateTime.now())
        );

        when(appointmentRepository.findById("ZXC456v"))
                .thenReturn(Optional.empty());

        assertThrowsExactly(AppointmentNotFoundException.class, () -> appointmentService.updateOnlyAppointmentInfos("ZXC456v", request));
    }

    @Test
    public void deleteAppointment_Success() {
        when(appointmentRepository.findById("ZXC456v"))
                .thenReturn(Optional.of(createValidAppointment()));

        doNothing().when(appointmentRepository).delete(any(Appointment.class));

        assertDoesNotThrow(() -> appointmentService.deleteAppointment("ZXC456v"));
    }

    @Test
    public void markAppointmentAsCompleted_Success() {
        Appointment appointment = createValidAppointment();

        when(appointmentRepository.findById("ZXC456v"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        AppointmentResponseDTO response = appointmentService.markAppointmentAsCompleted("ZXC456v");

        assertEquals("ZXC456v", response.id());
        assertEquals("COMPLETED", response.situation());
        assertNotNull(response.endAppointment());

    }

    @Test
    public void findAllAppointmentPayments_Success() {

        Appointment appointment = createValidAppointment();

        Payment payment = new Payment();
        payment.setId("PAYID");
        payment.setValue(BigDecimal.valueOf(125));
        payment.setPaymentType(PaymentType.PIX);
        payment.setAppointment(appointment);

        appointment.setPayments(List.of(payment));

        when(appointmentRepository.findById("ZXC456v")).
                thenReturn(Optional.of(appointment));

        List<PaymentResponseDTO> payments = appointmentService.findAllAppointmentPayments("ZXC456v");

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertInstanceOf(PaymentResponseDTO.class, payments.getFirst());
        assertEquals("PAYID", payments.getFirst().id());
    }

    @Test
    public void findAllAppointmentPayments_Returns_Empty_List() {

        Appointment appointment = createValidAppointment();
        appointment.setPayments(List.of());

        when(appointmentRepository.findById("ZXC456v")).
                thenReturn(Optional.of(appointment));

        List<PaymentResponseDTO> payments = appointmentService.findAllAppointmentPayments("ZXC456v");

        assertNotNull(payments);
        assertEquals(0, payments.size());
    }

}