package com.loanapp.loan_application.controller.loan;

import com.loanapp.loan_application.dto.loan.DisbursementRequest;
import com.loanapp.loan_application.dto.loan.DisbursementResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.loan.DisbursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/disbursement")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class DisbursementController {

    private final DisbursementService disbursementService;

    @PutMapping("/complete")
    public ResponseEntity<ApiResponse<DisbursementResponse>> disburse(
            @Valid @RequestBody DisbursementRequest request) {

        DisbursementResponse response = disbursementService.disburse(request);

        return ResponseEntity.ok(createApiResponse(
                response,
                "Loan disbursed successfully",
                "/v1/disbursement/complete"
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DisbursementResponse>>> getAllDisbursements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DisbursementResponse> response =
                disbursementService.getAllDisburse(size, page);

        return ResponseEntity.ok(createApiResponse(
                response,
                "Disbursements retrieved successfully",
                "/v1/disbursement"
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