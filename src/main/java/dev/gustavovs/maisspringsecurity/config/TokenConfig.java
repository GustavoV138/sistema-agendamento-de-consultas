package dev.gustavovs.maisspringsecurity.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import dev.gustavovs.maisspringsecurity.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TokenConfig {

    private final String secretKey;
    private final Algorithm algorithm;

    public TokenConfig(@Value("${jwt.secret:${JWT_SECRET:}}") String secretKey, Environment environment) {
        if (environment.matchesProfiles("demo")) {
            if (secretKey == null || secretKey.isBlank()) {
                throw new IllegalStateException("JWT_SECRET environment variable is required in demo profile!");
            }
        }
        if (secretKey == null || secretKey.isBlank()) {
            this.secretKey = "secret-secret-2-times";
        } else {
            this.secretKey = secretKey;
        }
        this.algorithm = Algorithm.HMAC256(this.secretKey);
    }

    public String generateToken(User user) {
        try {
            return JWT.create()
                    .withIssuer("mais-springsecurity")
                    .withClaim("userId", user.getId())
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .withIssuedAt(Instant.now())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error while generating token.", ex);
        }
    }

    public String validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer("mais-springsecurity")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (Exception ex) {
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("-03:00"));
    }
}
