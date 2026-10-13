package com.loanapp.loan_application.dto.supportticket;

import jakarta.validation.constraints.NotBlank;

public class SupportTicketStatusRequestDto {
    @NotBlank(message = "Status is required")
    private String status;
    public SupportTicketStatusRequestDto() {}
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
