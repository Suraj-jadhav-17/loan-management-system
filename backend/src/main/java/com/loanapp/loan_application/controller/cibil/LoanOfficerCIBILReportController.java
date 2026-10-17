
package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import com.loanapp.loan_application.service.cibil.CIBILReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/v1/loan-office/cibil-reports")
@RequiredArgsConstructor
public class LoanOfficerCIBILReportController {

    private final CIBILReportService cibilReportService;

    @GetMapping("/{customerId}/latest")
    public ResponseEntity<CIBILReportResponseDto> getLatestReport(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                cibilReportService.getLatestReport(customerId)
        );
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<Page<CIBILReportResponseDto>> getReportHistory(
            @PathVariable Long customerId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                cibilReportService.getReportHistory(customerId, pageable)
        );
    }
}
