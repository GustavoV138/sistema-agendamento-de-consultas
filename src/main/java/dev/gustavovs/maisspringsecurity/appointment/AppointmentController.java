package dev.gustavovs.maisspringsecurity.appointment;

import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.response.AppointmentResponseDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody @Valid AppointmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @GetMapping("/{situation}")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsBySituation(@PathVariable AppointmentSituation situation) {
        return ResponseEntity.ok(appointmentService.findAppointmentsBySituation(situation));
    }

    @GetMapping("/by/day/{day}")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByDay(@PathVariable int day) {
        return ResponseEntity.ok(appointmentService.findAppointmentsOnDay(day));
    }

    @GetMapping("/by/month/{month}")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByMonth(@PathVariable int month) {
        return ResponseEntity.ok(appointmentService.findAppointmentsOnMonth(month));
    }

    @GetMapping("/by/year/{year}")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByYear(@PathVariable int year) {
        return ResponseEntity.ok(appointmentService.findAppointmentsOnYear(year));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateOnlyAppointmentInfos(@PathVariable String id, @RequestBody AppointmentUpdateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.updateOnlyAppointmentInfos(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<Void> markAppointmentAsCompleted(@PathVariable String id){
        appointmentService.markAppointmentAsCompleted(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentResponseDTO>> findAllAppointmentPayments(@PathVariable String id) {
        List<PaymentResponseDTO> response = appointmentService.findAllAppointmentPayments(id);
        return ResponseEntity.ok(response);
    }
}
