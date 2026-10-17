package com.loanapp.loan_application.controller.Auth;

import com.loanapp.loan_application.dto.register.AuthFlowResponse;
import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.dto.register.VerifyOtpRequest;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.response.LoginResponse;
import com.loanapp.loan_application.service.registration.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegistrationRequest request,
            HttpSession session,
            HttpServletRequest httpRequest) {

        User savedUser = authService.register(request);

        session.setAttribute("registrationDone", true);
        session.setAttribute("email", request.getEmail());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", savedUser.getUserId());
        data.put("email", savedUser.getEmail());

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                "201",
                true,
                "Registration successful. Verify the OTP sent to your email.",
                data,
                LocalDateTime.now(),
                httpRequest.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verified-otp")
    public ResponseEntity<?> verifiedEmailOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpSession session) {

        Boolean registrationDone =
                (Boolean) session.getAttribute("registrationDone");

        String email = (String) session.getAttribute("email");

        if (!Boolean.TRUE.equals(registrationDone)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please complete registration first.");
        }

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration session expired. Please register again.");
        }

        boolean verified = authService.checkOtp(email, request);

        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP.");
        }

        session.removeAttribute("email");
        session.removeAttribute("registrationDone");

        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthFlowResponse> login(
            @Valid @RequestBody LoginDto request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/authenticator/setup")
    public ResponseEntity<AuthFlowResponse> setupAuthenticator(
            @RequestHeader("X-Verification-Token") String token) {

        return ResponseEntity.ok(
                authService.setupAuthenticator(token)
        );
    }

    @PostMapping("/authenticator/verify-setup")
    public ResponseEntity<LoginResponse> verifyAuthenticatorSetup(
            @RequestHeader("X-Verification-Token") String token,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.verifyAuthenticatorSetup(
                        token,
                        request.get("otp")
                )
        );
    }

    @PostMapping("/authenticator/verify-login")
    public ResponseEntity<LoginResponse> verifyAuthenticatorLogin(
            @RequestHeader("X-Verification-Token") String token,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.verifyAuthenticatorLogin(
                        token,
                        request.get("otp")
                )
        );
    }

    @PostMapping("/recovery/request")
    public ResponseEntity<Map<String, String>> requestRecovery(
            @RequestHeader("X-Verification-Token") String token) {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        authService.requestRecoveryOtp(token)
                )
        );
    }

    @PostMapping("/recovery/verify")
    public ResponseEntity<AuthFlowResponse> verifyRecovery(
            @RequestHeader("X-Verification-Token") String token,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                authService.verifyRecoveryOtp(
                        token,
                        request.get("otp")
                )
        );
    }
}