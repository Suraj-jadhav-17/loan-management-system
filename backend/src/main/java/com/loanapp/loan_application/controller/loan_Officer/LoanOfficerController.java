
package com.loanapp.loan_application.controller.loan_Officer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth/v1/loan-officer")
public class LoanOfficerController {

    private final CustomerService customerService;

    public LoanOfficerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> loanOfficerDashboard() {
        return ResponseEntity.ok("Welcome to Loan Officer Dashboard");
    }

    @GetMapping("/listCustomer")
    public ResponseEntity<ApiResponse<?>> getAllCustomer(
            HttpServletRequest request) {

        List<CustomerProfileResponse> customers =
                customerService.getAllCustomerList();

        if (customers == null || customers.isEmpty()) {
            throw new RuntimeException("No Any Customer Available");
        }

        ApiResponse<List<CustomerProfileResponse>> response =
                new ApiResponse<>(
                        "200",
                        true,
                        "Customer list fetched successfully",
                        customers,
                        LocalDateTime.now(),
                        request.getRequestURI(),
                        null
                );

        return ResponseEntity.ok(response);
    }
}

