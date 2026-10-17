package com.loanapp.loan_application.controller.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycCustomerSummaryResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentReviewRequestDto;
import com.loanapp.loan_application.service.kycdocument.LoanOfficerKycDocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/v1/loan-office/kyc")
public class LoanOfficerKycDocumentController {
    private final LoanOfficerKycDocumentService service;
    public LoanOfficerKycDocumentController(LoanOfficerKycDocumentService service) { this.service = service; }

    @GetMapping("/customers")
    public ResponseEntity<List<KycCustomerSummaryResponseDto>> customers() {
        return ResponseEntity.ok(service.getCustomersWithKycDocuments());
    }

    @GetMapping("/customers/{customerId}/documents")
    public ResponseEntity<List<KycDocumentResponseDto>> documents(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getCustomerDocuments(customerId));
    }

    @GetMapping("/customers/{customerId}/documents/{documentId}/file")
    public ResponseEntity<Resource> viewFile(@PathVariable Long customerId, @PathVariable Long documentId) {
        Resource resource = service.getCustomerDocumentFile(customerId, documentId);
        MediaType type = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(resource.getFilename() == null ? "document" : resource.getFilename(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString()).body(resource);
    }

    @PutMapping("/customers/{customerId}/documents/approve")
    public ResponseEntity<Map<String, String>> approve(@PathVariable Long customerId,
                                                        @Valid @RequestBody KycDocumentReviewRequestDto request) {
        service.approveDocuments(customerId, request);
        return ResponseEntity.ok(Map.of("message", "Selected KYC documents approved."));
    }

    @PutMapping("/customers/{customerId}/documents/reject")
    public ResponseEntity<Map<String, String>> reject(@PathVariable Long customerId,
                                                       @Valid @RequestBody KycDocumentReviewRequestDto request) {
        service.rejectDocuments(customerId, request);
        return ResponseEntity.ok(Map.of("message", "Selected KYC documents rejected and customer notification attempted."));
    }
}
