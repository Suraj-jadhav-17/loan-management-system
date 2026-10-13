package com.loanapp.loan_application.service.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycDocumentRequestDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycStatusResponseDto;
import org.springframework.core.io.Resource;
import java.util.List;

public interface CustomerKycDocumentService {
    List<String> getAvailableDocumentTypes(String customerEmail);
    KycDocumentResponseDto uploadDocument(String customerEmail, KycDocumentRequestDto request);
    List<KycDocumentResponseDto> getMyDocuments(String customerEmail);
    KycStatusResponseDto getMyKycStatus(String customerEmail);
    void deleteMyDocument(String customerEmail, Long documentId);
    Resource getMyDocumentFile(String customerEmail, Long documentId);
}
