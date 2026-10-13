package com.loanapp.loan_application.service.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KycDocumentService {

    List<String> getAvailableDocumentTypes(Long customerId);

    KycDocumentResponseDto uploadDocument(
            Long customerId,
            KycDocumentRequestDto request);

    List<KycDocumentResponseDto> getCustomerDocuments(
           Long customerId);

    KycStatusResponseDto getKycStatus(
           Long customerId);

    void deleteDocument(Integer documentId);

    List<KycCustomerSummaryResponseDto> getOfficerCustomers();

    List<KycDocumentResponseDto> getCustomerDocumentsForOfficer(
            Long customerId);

    void approveDocuments(
           Long customerId,
            KycDocumentReviewRequestDto request);

    void rejectDocuments(
            Long customerId,
            KycDocumentReviewRequestDto request);

    ResponseEntity<?> getDocumentFile(Integer documentId);
}