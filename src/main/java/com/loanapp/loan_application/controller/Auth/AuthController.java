package com.loanapp.loan_application.controller.Auth;


import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.dto.register.VerifyOtpRequest;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.response.LoginResponse;
import com.loanapp.loan_application.service.jwt.JwtService;
import com.loanapp.loan_application.service.registration.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            HttpSession session,
            HttpServletRequest request) {

        User savedUser = authService.register(registrationRequest);

        session.setAttribute("registrationDone", true);
        session.setAttribute("email", registrationRequest.getEmail());

        ApiResponse<User> response = new ApiResponse<>(
                "200",
                true,
                "Registration successful",
                savedUser,
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verified-otp")
    public ResponseEntity<?> verifiedEmailOtp(@RequestBody VerifyOtpRequest request, HttpSession session) {
        String customerEmail = (String) session.getAttribute("email");
        Boolean registrationDone =
                (Boolean) session.getAttribute("registrationDone");

        if (registrationDone == null || !registrationDone) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please complete registration first.");
        }

        if (customerEmail == null) {
            return ResponseEntity.badRequest().body("Registration session expired");
        }
        boolean verifiedOtp = authService.checkOtp(customerEmail, request);
        if (!verifiedOtp) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
        session.removeAttribute("email");

        return ResponseEntity.ok(
                "Email verified successfully"
        );

    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletRequest request) {

        LoginResponse loginUser = authService.login(loginDto);

        ApiResponse<LoginResponse> response = new ApiResponse<>(
                "200",
                true,
                "User Login Successfully",
                loginUser,
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }





}
