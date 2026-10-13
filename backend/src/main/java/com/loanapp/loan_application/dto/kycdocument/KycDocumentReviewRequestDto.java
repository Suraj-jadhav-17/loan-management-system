package com.loanapp.loan_application.dto.kycdocument;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class KycDocumentReviewRequestDto {
    @NotEmpty(message = "Select at least one document")
    private List<Long> documentIds;
    private String rejectionReason;
    public KycDocumentReviewRequestDto() {}
    public List<Long> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<Long> documentIds) { this.documentIds = documentIds; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
