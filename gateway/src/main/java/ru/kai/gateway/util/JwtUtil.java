package ru.kai.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${security.jwt-secret}")
    private String secret;

    public Mono<String> generateToken() {
        return Mono.fromSupplier(() ->
                Jwts.builder()
                        .setSubject("test")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                        .compact()
        );
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromSupplier(() -> {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }
}
