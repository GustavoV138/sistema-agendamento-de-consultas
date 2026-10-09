package dev.gustavovs.maisspringsecurity.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.gustavovs.maisspringsecurity.user.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TokenConfig {

    private final String SECRET_KEY = "secret-secret-2-times";

    Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

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
                    .withIssuer("mais-springsecurity") // Caso dê ruim, tira o withClaim(userId) e tenta denovo. Mas acho que funciona
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException ex) {
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("-03:00"));
    }
}
