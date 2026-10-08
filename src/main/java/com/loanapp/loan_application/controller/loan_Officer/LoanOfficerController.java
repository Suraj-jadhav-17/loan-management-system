package com.loanapp.loan_application.controller.loan_Officer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1/loan-officer")
public class LoanOfficerController {


    @GetMapping("/dashboard")
    public ResponseEntity<?> loanOfficerDashboard() {
        return ResponseEntity.ok("Welcome to Loan Officer Dashboard");
    }
}
