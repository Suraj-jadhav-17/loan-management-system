package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.EligibilityResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/customer/cibil/eligibility")
@RequiredArgsConstructor
public class CustomerEligibilityController {

    private final EligibilityResultService eligibilityResultService;
    private final CustomerRepository customerRepository;

    private Long getCustomerId(Authentication authentication) {

        Customer customer = customerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Customer not found"));
        return customer.getCustomerId();
    }

    @PostMapping("/check")
    public ResponseEntity<EligibilityResultResponseDto> checkEligibility(Authentication authentication) {

        EligibilityResultRequestDto request = new EligibilityResultRequestDto();
        request.setCustomerId(getCustomerId(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(eligibilityResultService.checkEligibility(request));
    }

    @GetMapping("/latest")
    public ResponseEntity<EligibilityResultResponseDto> getLatestEligibility(Authentication authentication) {
        return ResponseEntity.ok(eligibilityResultService.getLatestEligibility(getCustomerId(authentication)));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<EligibilityResultResponseDto>> getEligibilityHistory(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(eligibilityResultService.getEligibilityHistory(getCustomerId(authentication), pageable));
    }
}