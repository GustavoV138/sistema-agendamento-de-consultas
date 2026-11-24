package dev.gustavovs.maisspringsecurity.demo;

import dev.gustavovs.maisspringsecurity.appointment.Appointment;
import dev.gustavovs.maisspringsecurity.appointment.AppointmentRepository;
import dev.gustavovs.maisspringsecurity.appointment.AppointmentSituation;
import dev.gustavovs.maisspringsecurity.patient.Patient;
import dev.gustavovs.maisspringsecurity.patient.PatientRepository;
import dev.gustavovs.maisspringsecurity.payment.Payment;
import dev.gustavovs.maisspringsecurity.payment.PaymentRepository;
import dev.gustavovs.maisspringsecurity.payment.PaymentType;
import dev.gustavovs.maisspringsecurity.user.User;
import dev.gustavovs.maisspringsecurity.user.UserRepository;
import dev.gustavovs.maisspringsecurity.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Profile("demo")
public class DemoSeedService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoSeedService.class);

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReentrantLock lock = new ReentrantLock();

    public DemoSeedService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PaymentRepository paymentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Iniciando carga de dados (Seed) para o perfil DEMO...");
        resetAndSeed();
    }

    @Scheduled(fixedRateString = "${demo.reset.interval-ms:7200000}", initialDelayString = "${demo.reset.interval-ms:7200000}")
    public void scheduledReset() {
        log.info("Executando reset agendado do perfil DEMO...");
        resetAndSeed();
    }

    public void resetAndSeed() {
        lock.lock();
        try {
            executeResetAndSeed();
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void executeResetAndSeed() {
        log.info("Limpando tabelas para reset da demo...");
        paymentRepository.deleteAll();
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        userRepository.deleteAll();

        log.info("Populando dados do perfil demo...");

        // 1. Usuário Demo
        User demoUser = new User();
        demoUser.setName("Demo User");
        demoUser.setEmail("demo@portfolio.dev");
        demoUser.setPassword(passwordEncoder.encode("demo1234"));
        demoUser.setRole(UserRole.ADMIN);
        userRepository.save(demoUser);

        // 2. Três Pacientes
        LocalDate today = LocalDate.now();

        Patient p1 = new Patient();
        p1.setName("Ana Clara Silva");
        p1.setPhoneNumber("11987654321");
        p1.setBirthday(today.minusYears(28));
        p1 = patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setName("Bruno Henrique Santos");
        p2.setPhoneNumber("11987654322");
        p2.setBirthday(today.minusYears(35));
        p2 = patientRepository.save(p2);

        Patient p3 = new Patient();
        p3.setName("Camila Rodrigues Lima");
        p3.setPhoneNumber("11987654323");
        p3.setBirthday(today.minusYears(42));
        p3 = patientRepository.save(p3);

        // 3. Quatro Consultas ACTIVE em dias futuros distintos fora de 09:00 - 15:30 (ex: 07:00, 17:30, 07:30, 18:00)
        Appointment a1 = new Appointment();
        a1.setTitle("Consulta Dermatológica");
        a1.setDescription("Avaliação de rotina da pele");
        a1.setStartAppointment(today.plusDays(1).atTime(7, 0));
        a1.setSituation(AppointmentSituation.ACTIVE);
        a1.setPatient(p1);
        appointmentRepository.save(a1);

        Appointment a2 = new Appointment();
        a2.setTitle("Consulta Cardiológica");
        a2.setDescription("Check-up preventivo cardiológico");
        a2.setStartAppointment(today.plusDays(2).atTime(17, 30));
        a2.setSituation(AppointmentSituation.ACTIVE);
        a2.setPatient(p2);
        appointmentRepository.save(a2);

        Appointment a3 = new Appointment();
        a3.setTitle("Consulta Oftalmológica");
        a3.setDescription("Exame de acuidade visual");
        a3.setStartAppointment(today.plusDays(3).atTime(7, 30));
        a3.setSituation(AppointmentSituation.ACTIVE);
        a3.setPatient(p3);
        appointmentRepository.save(a3);

        Appointment a4 = new Appointment();
        a4.setTitle("Retorno Clínico Geral");
        a4.setDescription("Apresentação de exames laboratoriais");
        a4.setStartAppointment(today.plusDays(4).atTime(18, 0));
        a4.setSituation(AppointmentSituation.ACTIVE);
        a4.setPatient(p1);
        appointmentRepository.save(a4);

        // 4. Uma Consulta COMPLETED com pagamento PIX quitado e um pagamento pendente
        Appointment aCompleted = new Appointment();
        aCompleted.setTitle("Consulta Ortopédica");
        aCompleted.setDescription("Avaliação e tratamento articular");
        LocalDateTime completedStart = today.minusDays(1).atTime(17, 0);
        aCompleted.setStartAppointment(completedStart);
        aCompleted.setEndAppointment(completedStart.plusMinutes(45));
        aCompleted.setSituation(AppointmentSituation.COMPLETED);
        aCompleted.setPatient(p2);
        aCompleted = appointmentRepository.save(aCompleted);

        // Pagamento PIX quitado
        Payment payPix = new Payment();
        payPix.setValue(BigDecimal.valueOf(250.00));
        payPix.setPaymentType(PaymentType.PIX);
        payPix.setIsDone(true);
        payPix.setAppointment(aCompleted);
        paymentRepository.save(payPix);

        // Pagamento pendente
        Payment payPending = new Payment();
        payPending.setValue(BigDecimal.valueOf(100.00));
        payPending.setPaymentType(PaymentType.CREDIT_CARD);
        payPending.setIsDone(false);
        payPending.setAppointment(aCompleted);
        paymentRepository.save(payPending);

        log.info("Seed do perfil DEMO concluído com sucesso.");
    }
}
