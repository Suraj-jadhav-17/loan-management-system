package com.loanapp.loan_application.controller.Auth;


import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.response.LoginResponse;
import com.loanapp.loan_application.service.jwt.JwtService;
import com.loanapp.loan_application.service.registration.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {

        User savedUser = authService.register(registrationRequest);

        return ResponseEntity.ok(new ApiResponse("200", true, "Registration successful", savedUser));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        LoginResponse loginUser = authService.login(loginDto);
        return ResponseEntity.ok(new ApiResponse("200", true, "User Login Successfully", loginUser));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body("Token is missing");
//        }
//
//        String token  = authHeader.substring(7);
//        jwtService.logout(toString());
//
//
//        return ResponseEntity.ok("Logout successful");
//    }


}
