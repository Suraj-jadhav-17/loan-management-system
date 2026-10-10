package com.loanapp.loan_application.controller.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/v1/customer/")
public class CustomerController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> customerDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }
}
