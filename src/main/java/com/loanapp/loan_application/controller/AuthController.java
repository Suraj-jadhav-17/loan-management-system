package com.loanapp.loan_application.controller;

import com.loanapp.loan_application.dto.RegistrationRequest;
import com.loanapp.loan_application.entity.User;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {

        User savedUser = authService.register(registrationRequest);

        return ResponseEntity.ok(new ApiResponse("200", true, "Registration successful",savedUser));

    }
}
