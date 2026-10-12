package com.loanapp.loan_application.service.jwt;

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
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role) {

        return Jwts.builder().signWith(getSigningKey()).subject(email).claim("role", role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationTime)).compact();
    }

    public String extractEmail(String token) {

        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractRole(String token) {

        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String generateRefreshToken(String email) {

        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime)).signWith(getSigningKey()).compact();
    }

    public boolean isValidToken(String token) {

        try {
            extractEmail(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String refreshToken) {

        return isValidToken(refreshToken);
    }



}