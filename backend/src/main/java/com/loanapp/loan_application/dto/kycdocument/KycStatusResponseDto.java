package com.loanapp.loan_application.dto.kycdocument;

public class KycStatusResponseDto {

    private Integer customerId;
    private String status;
    private int totalDocuments;
    private int approvedDocuments;
    private int pendingDocuments;
    private int rejectedDocuments;
    private int requiredDocuments;

    public KycStatusResponseDto() {
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(int totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public int getApprovedDocuments() {
        return approvedDocuments;
    }

    public void setApprovedDocuments(int approvedDocuments) {
        this.approvedDocuments = approvedDocuments;
    }

    public int getPendingDocuments() {
        return pendingDocuments;
    }

    public void setPendingDocuments(int pendingDocuments) {
        this.pendingDocuments = pendingDocuments;
    }

    public int getRejectedDocuments() {
        return rejectedDocuments;
    }

    public void setRejectedDocuments(int rejectedDocuments) {
        this.rejectedDocuments = rejectedDocuments;
    }

    public int getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(int requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}