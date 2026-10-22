package dev.gustavovs.maisspringsecurity.appointment;

import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.request.AppointmentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.appointment.dto.response.AppointmentResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody @Valid AppointmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsBySituation(@RequestParam AppointmentSituation situation) {
        return ResponseEntity.ok(appointmentService.findAppointmentsBySituation(situation));
    }

    @GetMapping("/by/day")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByDay(@RequestParam int day) {
        return ResponseEntity.ok(appointmentService.findAppointmentsOnDay(day));
    }

    @GetMapping("/by/month")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByMonth(@RequestParam int month) {
        return ResponseEntity.ok(appointmentService.findAppointmentsOnMonth(month));
    }

    @GetMapping("/by/year")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsByYear(@RequestParam int year) {
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
}
