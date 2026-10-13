package com.loanapp.loan_application.controller.customer;

import com.loanapp.loan_application.dto.customer.CustomerProfileResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.customer.CustomerService;
import com.loanapp.loan_application.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth/v1/customer/")
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;

    public CustomerController(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> customerDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> customerProfile(HttpSession http, HttpServletRequest request) {

        String email = http.getAttribute("email").toString();

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            "401",
                            false,
                            "User session not found. Please login again.",
                            null,
                            LocalDateTime.now(), request.getRequestURI(), null));
        }
        CustomerProfileResponse user = customerService.findByProfile(email);


        return ResponseEntity.ok(new ApiResponse<>("200", true, "Success", user, LocalDateTime.now(), request.getRequestURI(), null));


    }

}