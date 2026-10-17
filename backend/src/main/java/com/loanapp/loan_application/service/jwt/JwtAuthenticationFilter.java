package com.loanapp.loan_application.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_AUTH_PATHS = Set.of(
            "/auth/v1/register",
            "/auth/v1/verified-otp",
            "/auth/v1/login",
            "/auth/v1/authenticator/setup",
            "/auth/v1/authenticator/verify-setup",
            "/auth/v1/authenticator/verify-login",
            "/auth/v1/recovery/request",
            "/auth/v1/recovery/verify"
    );

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || PUBLIC_AUTH_PATHS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7).trim();
        if (!jwtService.isValidToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token.isEmpty() || !jwtService.isValidToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is invalid or expired.");
            return;
        }

        String email = jwtService.extractEmail(token);
        String role = jwtService.extractRole(token);
        if (email == null || email.isBlank() || role == null || role.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is missing required claims.");
            return;
        }

        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        var authority = new SimpleGrantedAuthority(normalizedRole);
        var authentication = new UsernamePasswordAuthenticationToken(
                email, null, Collections.singleton(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
