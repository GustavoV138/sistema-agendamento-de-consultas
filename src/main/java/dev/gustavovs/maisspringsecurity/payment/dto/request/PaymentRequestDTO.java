package dev.gustavovs.maisspringsecurity.payment.dto.request;

import dev.gustavovs.maisspringsecurity.payment.PaymentType;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotEmpty(message = "Value cannot be empty/null.")
        BigDecimal value,

        @NotEmpty(message = "Payment type cannot be empty/null.")
        PaymentType paymentType,

        Boolean isDone
) {
}
