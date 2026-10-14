package com.loanapp.loan_application.controller.customer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;
import com.loanapp.loan_application.dto.cuatomer.EmiSummaryResponse;
import com.loanapp.loan_application.dto.dashBoard.CustomerDashboardResponse;
import com.loanapp.loan_application.dto.payment.LoanPaymentRecordDto;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.customer.CustomerService;
import com.loanapp.loan_application.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<ApiResponse<?>> customerDashboard(Authentication authentication, HttpServletRequest request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("401", false, "User is not authenticated", null, LocalDateTime.now(), request.getRequestURI(), null));
        }

        String email = authentication.getName();

        CustomerDashboardResponse response = customerService.getDashboard(email);

        return ResponseEntity.ok(new ApiResponse<>("200", true, "Customer Dashboard fetched successfully", response, LocalDateTime.now(), request.getRequestURI(), null));
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> customerProfile(Authentication authentication, HttpServletRequest request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("401", false, "Authentication required. Please login again.", null, LocalDateTime.now(), request.getRequestURI(), null));
        }

        String email = authentication.getName();

        CustomerProfileResponse user = customerService.findByProfile(email);

        return ResponseEntity.ok(new ApiResponse<>("200", true, "Customer profile fetched successfully", user, LocalDateTime.now(), request.getRequestURI(), null));
    }


    @GetMapping("/emi-summary")
    public ResponseEntity<ApiResponse<?>> getSummaryEmi(HttpServletRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("401", false, "User is not authenticated", null, LocalDateTime.now(), request.getRequestURI(), null));
        }
        String email = authentication.getName();
        EmiSummaryResponse response = customerService.getEmiSummary(email);
        return ResponseEntity.ok(new ApiResponse<>("200", true, "EMI summary fetched successfully", response, LocalDateTime.now(), request.getRequestURI(), null));


    }


    @GetMapping("/reciptsPayments")
    public ResponseEntity<?> reciptsPayments(HttpServletRequest request, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("401", false, "User is not authenticated", null, LocalDateTime.now(), request.getRequestURI(), null));
        }

        String email = authentication.getName();

        LoanPaymentRecordDto response = customerService.getReciptsPayment(email);

        return ResponseEntity.ok(new ApiResponse<>("200", true, "Record Successfully", response, LocalDateTime.now(), request.getRequestURI(), null));
    }


}