package com.nearkart.service;

import com.nearkart.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Secret key used to sign JWT tokens
    private static final String SECRET_KEY =
            "nearkart-secret-key-for-jwt-security-2026-this-key-is-long-enough";

    // Token validity: 24 hours
    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24;


    // =========================
    // GENERATE JWT TOKEN
    // =========================

    public String generateToken(User user) {

        return Jwts.builder()

                .subject(user.getEmail())

                .claim("userId", user.getId())

                .claim("role", user.getRole())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }


    // =========================
    // GET EMAIL FROM TOKEN
    // =========================

    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }


    // =========================
    // GET USER ID FROM TOKEN
    // =========================

    public Long extractUserId(String token) {

        Object userId =
                extractAllClaims(token)
                        .get("userId");

        if (userId instanceof Number) {

            return ((Number) userId).longValue();
        }

        return null;
    }


    // =========================
    // GET ROLE FROM TOKEN
    // =========================

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }


    // =========================
    // VALIDATE TOKEN
    // =========================

    public boolean isTokenValid(String token, User user) {

        String email = extractEmail(token);

        return email.equals(user.getEmail())
                && !isTokenExpired(token);
    }


    // =========================
    // CHECK TOKEN EXPIRATION
    // =========================

    private boolean isTokenExpired(String token) {

        Date expiration =
                extractAllClaims(token)
                        .getExpiration();

        return expiration.before(new Date());
    }


    // =========================
    // EXTRACT ALL CLAIMS
    // =========================

    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    // =========================
    // GET SIGNING KEY
    // =========================

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }
}