package dev.gustavovs.maisspringsecurity.payment.dto.request;

import dev.gustavovs.maisspringsecurity.payment.PaymentType;

import java.math.BigDecimal;
import java.util.Optional;

public record PaymentUpdateRequestDTO(
        Optional<BigDecimal> value,
        Optional<PaymentType> paymentType
) {
}
