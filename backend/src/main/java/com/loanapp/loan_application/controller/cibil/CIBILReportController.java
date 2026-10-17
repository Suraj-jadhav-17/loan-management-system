
package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.CIBILReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/v1/customer/cibil-reports")
@RequiredArgsConstructor
public class CIBILReportController {

    private final CIBILReportService cibilReportService;
    private final CustomerRepository customerRepository;

    private Long getCustomerId(Authentication authentication) {
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return customer.getCustomerId();
    }

    @PostMapping
    public ResponseEntity<CIBILReportResponseDto> createReport(Authentication authentication) {

        Long customerId = getCustomerId(authentication);

        CIBILReportRequestDto request = CIBILReportRequestDto.builder().customerId(customerId).build();

        CIBILReportResponseDto response = cibilReportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<CIBILReportResponseDto> getLatestReport(Authentication authentication) {

        Long customerId = getCustomerId(authentication);
        return ResponseEntity.ok(cibilReportService.getLatestReport(customerId));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<CIBILReportResponseDto>> getReportHistory(Authentication authentication, Pageable pageable) {

        Long customerId = getCustomerId(authentication);
        return ResponseEntity.ok(cibilReportService.getReportHistory(customerId, pageable));
    }
}
