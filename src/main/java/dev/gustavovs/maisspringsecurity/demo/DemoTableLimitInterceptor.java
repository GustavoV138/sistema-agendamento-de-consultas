package dev.gustavovs.maisspringsecurity.demo;

import dev.gustavovs.maisspringsecurity.appointment.AppointmentRepository;
import dev.gustavovs.maisspringsecurity.exception.exceptions.DemoLimitExceededException;
import dev.gustavovs.maisspringsecurity.patient.PatientRepository;
import dev.gustavovs.maisspringsecurity.payment.PaymentRepository;
import dev.gustavovs.maisspringsecurity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Profile("demo")
public class DemoTableLimitInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final long maxRecords;

    public DemoTableLimitInterceptor(
            UserRepository userRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PaymentRepository paymentRepository,
            @Value("${demo.table-limit.max-records:500}") long maxRecords) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.maxRecords = maxRecords;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();
            if (uri.startsWith("/appointment")) {
                if (appointmentRepository.count() >= maxRecords || patientRepository.count() >= maxRecords) {
                    throw new DemoLimitExceededException("limite da demo atingido");
                }
            } else if (uri.startsWith("/payment")) {
                if (paymentRepository.count() >= maxRecords) {
                    throw new DemoLimitExceededException("limite da demo atingido");
                }
            } else if (uri.startsWith("/auth/register")) {
                if (userRepository.count() >= maxRecords) {
                    throw new DemoLimitExceededException("limite da demo atingido");
                }
            }
        }
        return true;
    }
}
