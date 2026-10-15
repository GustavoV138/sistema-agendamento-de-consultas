package dev.gustavovs.maisspringsecurity.appointment;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import dev.gustavovs.maisspringsecurity.patient.Patient;
import dev.gustavovs.maisspringsecurity.payment.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    private String id;

    @Column(length = 200)
    private String title;

    @Column(length = 1500)
    private String description;

    private LocalDateTime startAppointment;

    private LocalDateTime endAppointment;

    @Enumerated(EnumType.STRING)
    private AppointmentSituation situation;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ToString.Exclude
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @PrePersist
    public void onCreate() {
        if(this.id == null){
            this.id = NanoIdUtils.randomNanoId();
        }

        this.situation = AppointmentSituation.ACTIVE;
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setAppointment(this);
    }

    public void removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setAppointment(null);
    }
}
