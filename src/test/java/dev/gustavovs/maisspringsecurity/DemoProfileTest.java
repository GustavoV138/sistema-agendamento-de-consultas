package dev.gustavovs.maisspringsecurity;

import dev.gustavovs.maisspringsecurity.appointment.Appointment;
import dev.gustavovs.maisspringsecurity.appointment.AppointmentRepository;
import dev.gustavovs.maisspringsecurity.appointment.AppointmentSituation;
import dev.gustavovs.maisspringsecurity.demo.DemoSeedService;
import dev.gustavovs.maisspringsecurity.patient.PatientRepository;
import dev.gustavovs.maisspringsecurity.payment.Payment;
import dev.gustavovs.maisspringsecurity.payment.PaymentRepository;
import dev.gustavovs.maisspringsecurity.payment.PaymentType;
import dev.gustavovs.maisspringsecurity.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-key-for-demo-profile-1234567890",
        "demo.table-limit.max-records=10"
})
@ActiveProfiles("demo")
class DemoProfileTest {

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final DemoSeedService demoSeedService;

    @Autowired
    public DemoProfileTest(
            WebApplicationContext context,
            UserRepository userRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PaymentRepository paymentRepository,
            DemoSeedService demoSeedService) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.demoSeedService = demoSeedService;
    }

    @Test
    void shouldSeedDeterministicDataOnStartup() {
        assertEquals(1, userRepository.count());
        assertTrue(userRepository.findByEmail("demo@portfolio.dev").isPresent());

        assertEquals(3, patientRepository.count());

        List<Appointment> appointments = appointmentRepository.findAll();
        assertEquals(5, appointments.size());

        long activeCount = appointments.stream()
                .filter(a -> a.getSituation() == AppointmentSituation.ACTIVE)
                .count();
        assertEquals(4, activeCount);

        for (Appointment app : appointments) {
            if (app.getSituation() == AppointmentSituation.ACTIVE) {
                assertTrue(app.getStartAppointment().toLocalDate().isAfter(LocalDate.now()));
                LocalTime time = app.getStartAppointment().toLocalTime();
                boolean isOutsideForbiddenHours = time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(15, 30));
                assertTrue(isOutsideForbiddenHours, "Appointment time should be outside 09:00 - 15:30: " + time);
            }
        }

        Appointment completed = appointments.stream()
                .filter(a -> a.getSituation() == AppointmentSituation.COMPLETED)
                .findFirst()
                .orElseThrow();
        assertEquals(AppointmentSituation.COMPLETED, completed.getSituation());

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(2, payments.size());

        boolean hasPixDone = payments.stream()
                .anyMatch(p -> p.getPaymentType() == PaymentType.PIX && Boolean.TRUE.equals(p.getIsDone()));
        boolean hasPending = payments.stream()
                .anyMatch(p -> Boolean.FALSE.equals(p.getIsDone()));
        assertTrue(hasPixDone);
        assertTrue(hasPending);
    }

    @Test
    void scheduledResetShouldCleanAndReseed() {
        demoSeedService.resetAndSeed();
        assertEquals(1, userRepository.count());
        assertEquals(3, patientRepository.count());
        assertEquals(5, appointmentRepository.findAll().size());
        assertEquals(2, paymentRepository.findAll().size());
    }

    @Test
    @WithMockUser
    void shouldReturn429WhenTableLimitReached() throws Exception {
        mockMvc.perform(post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Nova consulta",
                                    "startAppointment": "2026-12-01T07:00:00",
                                    "patientName": "Paciente Teste",
                                    "patientPhoneNumber": "11999990000",
                                    "patientBirthday": "1990-01-01"
                                }
                                """))
                .andExpect(status().isCreated());

        for (int i = 0; i < 15; i++) {
            dev.gustavovs.maisspringsecurity.patient.Patient p = new dev.gustavovs.maisspringsecurity.patient.Patient();
            p.setName("Dummy " + i);
            p.setPhoneNumber("119000000" + (i < 10 ? "0" + i : i));
            p.setBirthday(LocalDate.of(1990, 1, 1));
            patientRepository.save(p);
        }

        mockMvc.perform(post("/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Consulta Bloqueada",
                                    "startAppointment": "2026-12-02T07:00:00",
                                    "patientName": "Paciente Excedido",
                                    "patientPhoneNumber": "11999990001",
                                    "patientBirthday": "1990-01-01"
                                }
                                """))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.detail").value("limite da demo atingido"));

        demoSeedService.resetAndSeed();
    }
}
