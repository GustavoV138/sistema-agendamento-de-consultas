package dev.gustavovs.maisspringsecurity.payment;

import dev.gustavovs.maisspringsecurity.appointment.Appointment;
import dev.gustavovs.maisspringsecurity.appointment.AppointmentRepository;
import dev.gustavovs.maisspringsecurity.exception.exceptions.AppointmentNotFoundException;
import dev.gustavovs.maisspringsecurity.exception.exceptions.PaymentNotFoundException;
import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    PaymentService(PaymentRepository paymentRepository, AppointmentRepository appointmentRepository) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public PaymentResponseDTO createPayment(String appointmentId, PaymentRequestDTO request) {

        Appointment appointmentById = findAppointmentByIdInternal(appointmentId);

        Payment payment = new Payment();
        payment.setValue(request.value());
        payment.setPaymentType(request.paymentType());
        payment.setIsDone(request.isDone());
        payment.setAppointment(appointmentById);

        return toPaymentResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDTO updatePayment(String id, PaymentUpdateRequestDTO request) {

        Payment paymentFound = findPaymentByIdInternal(id);

        if (request.value().isPresent()) paymentFound.setValue(request.value().get());
        if (request.paymentType().isPresent()) paymentFound.setPaymentType(request.paymentType().get());

        return toPaymentResponse(paymentRepository.save(paymentFound));
    }

    public void confirmPayment(String id) {
        Payment paymentById = findPaymentByIdInternal(id);

        if (!paymentById.getIsDone()) paymentById.setIsDone(true);
    }

    public void deletePayment(String id) {
        Payment paymentById = findPaymentByIdInternal(id);
        paymentRepository.delete(paymentById);
    }


    private Payment findPaymentByIdInternal(String id) {
        Optional<Payment> paymentById = paymentRepository.findById(id);

        if (paymentById.isPresent()) return paymentById.get();
        else throw new PaymentNotFoundException(id);
    }

    private Appointment findAppointmentByIdInternal(String id) {
        Optional<Appointment> appointmentById = appointmentRepository.findById(id);

        if (appointmentById.isPresent()) return appointmentById.get();
        else throw new AppointmentNotFoundException(id);
    }

    private PaymentResponseDTO toPaymentResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getValue(),
                payment.getPaymentType(),
                payment.getIsDone()
        );
    }
}
