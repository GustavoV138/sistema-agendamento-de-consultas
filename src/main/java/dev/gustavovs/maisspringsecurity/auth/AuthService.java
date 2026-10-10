package dev.gustavovs.maisspringsecurity.auth;

import dev.gustavovs.maisspringsecurity.auth.dto.AuthResponse;
import dev.gustavovs.maisspringsecurity.commom.exception.EmailAlreadyInUseException;
import dev.gustavovs.maisspringsecurity.config.TokenConfig;
import dev.gustavovs.maisspringsecurity.user.User;
import dev.gustavovs.maisspringsecurity.user.UserRepository;
import dev.gustavovs.maisspringsecurity.user.UserRole;
import dev.gustavovs.maisspringsecurity.user.dto.request.UserLoginRequestDTO;
import dev.gustavovs.maisspringsecurity.user.dto.request.UserRegisterRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenConfig tokenConfig;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenConfig tokenConfig, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(UserRegisterRequestDTO request) {
        Optional<UserDetails> findUser = userRepository.findByEmail(request.email());
        if(findUser.isPresent()) throw new EmailAlreadyInUseException("Email already in use.");

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        User userSaved = userRepository.save(user);
        String token = tokenConfig.generateToken(userSaved);

        return new AuthResponse(token, userSaved.getName());
    }

    public AuthResponse login(UserLoginRequestDTO request) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authenticate = authenticationManager.authenticate(userAndPass);

        User user = (User) authenticate.getPrincipal();

        String token = tokenConfig.generateToken(user);

        return new AuthResponse(token, user.getName());
    }
}
