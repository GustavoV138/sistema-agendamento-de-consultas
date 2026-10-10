package dev.gustavovs.maisspringsecurity.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDTO(
        @NotEmpty(message = "Name must be provided.")
        String name,

        @NotEmpty(message = "Email must be provided.")
        @Email(message = "Email must follow emails pattern (example@email.com).")
        String email,

        @NotEmpty(message = "Password must be provided.")
        @Size(message = "Password must have a minimum of 4 characters.", min = 4)
        String password) {
}
