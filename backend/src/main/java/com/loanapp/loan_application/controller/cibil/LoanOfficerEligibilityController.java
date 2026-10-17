
package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.entity.cibil.OfficerReviewDecision;
import com.loanapp.loan_application.service.cibil.EligibilityResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/v1/loan-office/eligibility")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class LoanOfficerEligibilityController {

    private final EligibilityResultService eligibilityResultService;

    @PostMapping("/{customerId}/check")
    public ResponseEntity<EligibilityResultResponseDto> checkEligibility(@PathVariable Long customerId) {

        EligibilityResultRequestDto request = new EligibilityResultRequestDto();
        request.setCustomerId(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eligibilityResultService.checkEligibility(request));
    }

    @GetMapping("/{customerId}/latest")
    public ResponseEntity<EligibilityResultResponseDto> getLatestEligibility(@PathVariable Long customerId) {

        return ResponseEntity.ok(eligibilityResultService.getLatestEligibility(customerId));
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<Page<EligibilityResultResponseDto>> getEligibilityHistory(@PathVariable Long customerId, Pageable pageable) {

        return ResponseEntity.ok(eligibilityResultService.getEligibilityHistory(customerId, pageable));
    }

    @PutMapping("/{customerId}/review")
    public ResponseEntity<EligibilityResultResponseDto> reviewEligibility(@PathVariable Long customerId, @RequestParam OfficerReviewDecision decision) {

        return ResponseEntity.ok(eligibilityResultService.reviewEligibility(customerId, decision));
    }
}
