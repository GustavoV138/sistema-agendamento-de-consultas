package dev.gustavovs.maisspringsecurity.payment;

import dev.gustavovs.maisspringsecurity.appointment.Appointment;
import dev.gustavovs.maisspringsecurity.commom.BaseEnity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "payment")
public class Payment extends BaseEnity {

    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    private Boolean isDone;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

}
