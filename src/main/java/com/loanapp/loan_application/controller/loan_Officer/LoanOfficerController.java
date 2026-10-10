package com.loanapp.loan_application.controller.loan_Officer;

import com.loanapp.loan_application.response.CustomerResponse;
import com.loanapp.loan_application.service.loanOfficer.LoanOfficerService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/auth/v1/loan-officer")
@Validated
public class LoanOfficerController {
private final LoanOfficerService loanOfficerService;

    public LoanOfficerController(LoanOfficerService loanOfficerService) {
        this.loanOfficerService = loanOfficerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> loanOfficerDashboard() {

        return ResponseEntity.ok("Welcome to Loan Officer Dashboard");
    }

    @GetMapping("/listCustomer")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomer(
            @RequestParam(defaultValue = "0")
            @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(0) @Max(10) int limit,
            @RequestParam(defaultValue = "createdAt")
            String sortBy,
            @RequestParam(defaultValue = "asc")
            String direction

    ){


        return ResponseEntity.ok(
                loanOfficerService.getAllLoans(
                        page, limit, sortBy, direction));
    }
    }


