package com.loanapp.loan_application.controller.loanOfficer;

import com.loanapp.loan_application.dto.user.UserProfileResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.response.CustomerResponse;
import com.loanapp.loan_application.service.loanOfficer.LoanOfficerService;
import com.loanapp.loan_application.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/v1/loan-officer")
@Validated
public class LoanOfficerController {
    private final LoanOfficerService loanOfficerService;
    private final UserService userService;

    public LoanOfficerController(LoanOfficerService loanOfficerService, UserService userService) {
        this.loanOfficerService = loanOfficerService;
        this.userService = userService;

    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> loanOfficerDashboard() {

        return ResponseEntity.ok("Welcome to Loan Officer Dashboard");
    }

    @GetMapping("/listCustomer")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomer(@RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "10") @Min(0) @Max(10) int limit, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "asc") String direction

    ) {

        return ResponseEntity.ok(loanOfficerService.getAllLoans(page, limit, sortBy, direction));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> profile(HttpSession session, HttpServletRequest request) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            "401",
                            false,
                            "User session not found. Please login again.",
                            null,
                            LocalDateTime.now(),
                            request.getRequestURI(),
                            null
                    ));
        }
        UserProfileResponse user = userService.findByProfile(email);

        return ResponseEntity.ok(new ApiResponse<>("200",
                true,
                "Success",
                user,
                LocalDateTime.now(),
                request.getRequestURI(),
                null));


    }
    }



