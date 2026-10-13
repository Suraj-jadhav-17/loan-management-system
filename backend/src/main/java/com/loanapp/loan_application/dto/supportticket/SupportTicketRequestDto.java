package com.loanapp.loan_application.dto.supportticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class SupportTicketRequestDto {
    private Long loanAccountId;
    @NotBlank(message = "Subject is required")
    @Size(max = 255, message = "Subject must be at most 255 characters")
    private String subject;
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;
    private MultipartFile attachment;
    public SupportTicketRequestDto() {}
    public Long getLoanAccountId() { return loanAccountId; }
    public void setLoanAccountId(Long loanAccountId) { this.loanAccountId = loanAccountId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MultipartFile getAttachment() { return attachment; }
    public void setAttachment(MultipartFile attachment) { this.attachment = attachment; }
}
