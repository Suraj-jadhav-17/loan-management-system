package com.loanapp.loan_application.controller.loan;

import com.loanapp.loan_application.dto.loan.LoanDealRequestDto;
import com.loanapp.loan_application.dto.loan.LoanDealResponseDto;
import com.loanapp.loan_application.entity.loan.LoanType;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.loan.LoanDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("v1/loandeal")
@RequiredArgsConstructor
public class LoanDealController {

    private final LoanDealService loanDealService;
    @PostMapping
    public ResponseEntity<?> createLoanDeal(@RequestBody LoanDealRequestDto request){
        LoanDealResponseDto data = loanDealService.createLoanDeal(request);
        ApiResponse<?> response= null;
        if(data != null) {
           response = createApiResponse(
                    data,
                    "LoanDeal  Created",
                    null,
                    "v1/loandeal",
                    "Success",
                   true
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        response = createApiResponse(
                null,
                "Failed To Create Loan Deal",
                null,
                "v1/loandeal",
                "Failed",
                false
                );
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanDealResponseDto>> getLoanDealById(
            @PathVariable Long id) {

        LoanDealResponseDto data = loanDealService.getLoanDealById(id);

        ApiResponse<LoanDealResponseDto> response = createApiResponse(
                data,
                "Loan deal fetched successfully",
                null,
                "/v1/loandeal/" + id,
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<LoanDealResponseDto>>> getLoanDealsByCustomerId(
            @PathVariable Long customerId) {

        List<LoanDealResponseDto> data =
                loanDealService.getLoanDealByCustomerId(customerId);

        ApiResponse<List<LoanDealResponseDto>> response = createApiResponse(
                data,
                "Customer loan deals fetched successfully",
                null,
                "/v1/loandeal/customer/" + customerId,
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/type/{loanType}")
    public ResponseEntity<ApiResponse<List<LoanDealResponseDto>>> getLoanDealsByLoanType(
            @PathVariable LoanType loanType) {

        List<LoanDealResponseDto> data = loanDealService.getLoanDealByLoanType(loanType);

        ApiResponse<List<LoanDealResponseDto>> response = createApiResponse(
                data,
                "Loan deals fetched successfully",
                null,
                "/v1/loandeal/type/" + loanType,
                "SUCCESS",
                true
        );

        return ResponseEntity.ok(response);
    }

    private <T> ApiResponse<T> createApiResponse(
            T data,
            String message,
            Object metadata,
            String path,
            String status,
            boolean success) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(data);
        apiResponse.setMessage(message);
        apiResponse.setMetadata(metadata);
        apiResponse.setPath(path);
        apiResponse.setStatus(status);
        apiResponse.setSuccess(success);
        apiResponse.setTimestamp(LocalDateTime.now());

        return apiResponse;
    }



}
