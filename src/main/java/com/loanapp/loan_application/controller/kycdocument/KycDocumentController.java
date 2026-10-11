package com.loanapp.loan_application.controller.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.*;
import com.loanapp.loan_application.service.kycdocument.KycDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class KycDocumentController {

    private final KycDocumentService kycDocumentService;

    public KycDocumentController(
            KycDocumentService kycDocumentService) {

        this.kycDocumentService = kycDocumentService;
    }

    @GetMapping(
            "/customer/{customerId}/available-document-types")
    public ResponseEntity<List<String>>
    getAvailableDocumentTypes(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                kycDocumentService
                        .getAvailableDocumentTypes(customerId)
        );
    }

    @PostMapping(
            value = "/customer/{customerId}/documents",
            consumes = "multipart/form-data")
    public ResponseEntity<KycDocumentResponseDto>
    uploadDocument(
            @PathVariable Integer customerId,
            @ModelAttribute KycDocumentRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        kycDocumentService.uploadDocument(
                                customerId,
                                request
                        )
                );
    }

    @GetMapping("/customer/{customerId}/documents")
    public ResponseEntity<List<KycDocumentResponseDto>>
    getCustomerDocuments(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                kycDocumentService
                        .getCustomerDocuments(customerId)
        );
    }

    @GetMapping("/customer/{customerId}/status")
    public ResponseEntity<KycStatusResponseDto>
    getKycStatus(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                kycDocumentService
                        .getKycStatus(customerId)
        );
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void>
    deleteDocument(
            @PathVariable Integer documentId) {

        kycDocumentService.deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/officer/customers")
    public ResponseEntity<List<KycCustomerSummaryResponseDto>>
    getOfficerCustomers() {

        return ResponseEntity.ok(
                kycDocumentService.getOfficerCustomers()
        );
    }

    @GetMapping("/officer/customer/{customerId}/documents")
    public ResponseEntity<List<KycDocumentResponseDto>>
    getCustomerDocumentsForOfficer(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                kycDocumentService
                        .getCustomerDocumentsForOfficer(customerId)
        );
    }

    @PutMapping(
            "/officer/customer/{customerId}/documents/approve")
    public ResponseEntity<String>
    approveDocuments(
            @PathVariable Integer customerId,
            @RequestBody KycDocumentReviewRequestDto request) {

        kycDocumentService
                .approveDocuments(customerId, request);

        return ResponseEntity.ok(
                "Selected KYC documents approved successfully."
        );
    }

    @PutMapping(
            "/officer/customer/{customerId}/documents/reject")
    public ResponseEntity<String>
    rejectDocuments(
            @PathVariable Integer customerId,
            @RequestBody KycDocumentReviewRequestDto request) {

        kycDocumentService
                .rejectDocuments(customerId, request);

        return ResponseEntity.ok(
                "Selected KYC documents rejected successfully."
        );
    }

    @GetMapping("/documents/{documentId}/file")
    public ResponseEntity<?> getDocumentFile(
            @PathVariable Integer documentId) {

        return kycDocumentService.getDocumentFile(documentId);
    }
}