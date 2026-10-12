package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.serviceimpl.cibil.EligibilityResultServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/v1/loan-office/eligibilty")
@RequiredArgsConstructor
public class LoanOfficerEligibilityController {

    private final EligibilityResultServiceImpl eligibilityResultService;

    @PostMapping("/customer_id}/check")
    ResponseEntity<EligibilityResultResponseDto> checkEligibility(@PathVariable Long customerId){
        EligibilityResultRequestDto request=new EligibilityResultRequestDto();

        request.setCustomerId(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eligibilityResultService.checkEligibility(request));
    }

    @GetMapping("/customer_id}/latest")
    public ResponseEntity<EligibilityResultResponseDto> getLatestEligibility(@PathVariable Long customerId){
        return ResponseEntity.ok(eligibilityResultService.getLatestEligibility(customerId));
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<Page<EligibilityResultResponseDto>> getEligibilityHistory(@PathVariable Long customerId, Pageable pageable) {
        return ResponseEntity.ok(eligibilityResultService.getEligibilityHistory(customerId, pageable));
    }



}
