
package com.loanapp.loan_application.service.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycCustomerSummaryResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentReviewRequestDto;

import org.springframework.core.io.Resource;

import java.util.List;

public interface LoanOfficerKycDocumentService {

    List<KycCustomerSummaryResponseDto> getCustomersWithKycDocuments();

    List<KycDocumentResponseDto> getCustomerDocuments(Long customerId);

    Resource getCustomerDocumentFile(Long customerId, Long documentId);

    void approveDocuments(
            Long customerId,
            KycDocumentReviewRequestDto request
    );

    void rejectDocuments(
            Long customerId,
            KycDocumentReviewRequestDto request
    );
}
