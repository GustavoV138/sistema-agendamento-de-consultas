package dev.gustavovs.maisspringsecurity.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserLoginRequestDTO(
        @NotEmpty(message = "Email must be provided.")
        @Email(message = "Email must follow emails pattern (example@email.com).")
        String email,

        @NotEmpty(message = "Password must be provided.")
        String password
) {
}
