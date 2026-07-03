package com.example.authenticationsystem.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key (minimum 32 characters)
    private static final String SECRET =
            "authenticationSystemSecretKey2026SpringBootJWT";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token valid for 1 hour
    private final long EXPIRATION = 1000 * 60 * 60;

    // Generate JWT
    public String generateToken(String username) {

        return Jwts.builder()

                .subject(username)

                .issuedAt(new Date())

                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))

                .signWith(key)

                .compact();
    }

    // Extract username
    public String extractUsername(String token) {

        Claims claims = Jwts.parser()

                .verifyWith(key)

                .build()

                .parseSignedClaims(token)

                .getPayload();

        return claims.getSubject();
    }

    // Validate token
    public boolean validateToken(String token) {

        try {

            Jwts.parser()

                    .verifyWith(key)

                    .build()

                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}
