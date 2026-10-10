package dev.gustavovs.maisspringsecurity.auth;

import dev.gustavovs.maisspringsecurity.auth.dto.AuthResponse;
import dev.gustavovs.maisspringsecurity.user.dto.request.UserLoginRequestDTO;
import dev.gustavovs.maisspringsecurity.user.dto.request.UserRegisterRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequestDTO request) {
        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequestDTO request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

}
