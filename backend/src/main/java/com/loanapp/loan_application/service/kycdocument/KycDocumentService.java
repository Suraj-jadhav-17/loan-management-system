package com.loanapp.loan_application.service.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KycDocumentService {

    List<String> getAvailableDocumentTypes(Integer customerId);

    KycDocumentResponseDto uploadDocument(
            Integer customerId,
            KycDocumentRequestDto request);

    List<KycDocumentResponseDto> getCustomerDocuments(
            Integer customerId);

    KycStatusResponseDto getKycStatus(
            Integer customerId);

    void deleteDocument(Integer documentId);

    List<KycCustomerSummaryResponseDto> getOfficerCustomers();

    List<KycDocumentResponseDto> getCustomerDocumentsForOfficer(
            Integer customerId);

    void approveDocuments(
            Integer customerId,
            KycDocumentReviewRequestDto request);

    void rejectDocuments(
            Integer customerId,
            KycDocumentReviewRequestDto request);

    ResponseEntity<?> getDocumentFile(Integer documentId);
}