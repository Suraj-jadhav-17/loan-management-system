package com.loanapp.loan_application.dto.kycdocument;

import java.util.List;

public class KycDocumentReviewRequestDto {

    private List<Integer> documentIds;
    private String rejectionReason;

    public KycDocumentReviewRequestDto() {
    }

    public List<Integer> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}