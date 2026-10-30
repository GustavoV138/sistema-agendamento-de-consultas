package dev.gustavovs.maisspringsecurity.payment.dto.response;

import dev.gustavovs.maisspringsecurity.payment.PaymentType;

import java.math.BigDecimal;

public record PaymentResponseDTO(
        String id,
        BigDecimal value,
        PaymentType paymentType,
        Boolean isDone
) {
}
