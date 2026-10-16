package com.loanapp.loan_application.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpirationTime;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String email, String role) {
        return buildToken(email, role, "ACCESS", expirationTime);
    }

    public String generateRefreshToken(String email) {
        return buildToken(
                email,
                null,
                "REFRESH",
                refreshTokenExpirationTime
        );
    }

    public String generateVerificationToken(
            String email,
            String purpose
    ) {
        return buildToken(email, null, purpose, 300000);
    }

    private String buildToken(
            String email,
            String role,
            String purpose,
            long expiration
    ) {
        var builder = Jwts.builder()
                .subject(email)
                .claim("purpose", purpose)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + expiration
                ));

        if (role != null) {
            builder.claim("role", role);
        }

        return builder
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractPurpose(String token) {
        return extractClaims(token).get("purpose", String.class);
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = extractClaims(token);

            return "ACCESS".equals(
                    claims.get("purpose", String.class)
            );
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isValidVerificationToken(
            String token,
            String expectedPurpose
    ) {
        try {
            Claims claims = extractClaims(token);

            return expectedPurpose.equals(
                    claims.get("purpose", String.class)
            );
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);

            return "REFRESH".equals(
                    claims.get("purpose", String.class)
            );
        } catch (Exception ex) {
            return false;
        }
    }
}