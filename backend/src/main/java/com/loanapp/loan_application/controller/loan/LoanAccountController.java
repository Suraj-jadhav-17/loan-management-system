package com.loanapp.loan_application.controller.loan;

import com.loanapp.loan_application.dto.loan.LoanAccountResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.loan.LoanAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/loan-account")
@RequiredArgsConstructor
public class LoanAccountController {

    private final LoanAccountService loanAccountService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LoanAccountResponse>>> getAllLoanAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LoanAccountResponse> accounts =
                loanAccountService.getAllLoanAccount(size, page);

        return ResponseEntity.ok(createApiResponse(
                accounts,
                "Loan accounts retrieved successfully",
                "/v1/loan-account"
        ));
    }

    @GetMapping("/deal/{loanDealId}")
    public ResponseEntity<ApiResponse<LoanAccountResponse>> getLoanAccountByLoanDeal(
            @PathVariable Long loanDealId) {

        LoanAccountResponse account =
                loanAccountService.getLoanAccountByLoanDeal(loanDealId);

        return ResponseEntity.ok(createApiResponse(
                account,
                "Loan account retrieved successfully",
                "/v1/loan-account/deal/" + loanDealId
        ));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<LoanAccountResponse>>> getLoanAccountsByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LoanAccountResponse> accounts =
                loanAccountService.getLoanAccountsByCustomer(customerId, size, page);

        return ResponseEntity.ok(createApiResponse(
                accounts,
                "Customer loan accounts retrieved successfully",
                "/v1/loan-account/customer/" + customerId
        ));
    }

    private <T> ApiResponse<T> createApiResponse(
            T data,
            String message,
            String path) {

        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("SUCCESS");
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);
        response.setMetadata(null);

        return response;
    }
}