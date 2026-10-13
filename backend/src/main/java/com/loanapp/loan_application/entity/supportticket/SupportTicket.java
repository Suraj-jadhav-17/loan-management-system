package com.loanapp.loan_application.entity.supportticket;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "SupportTickets")
public class SupportTicket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketId")
    private Long ticketId;

    @Column(name = "CustomerId", nullable = false)
    private Long customerId;

    @Column(name = "LoanAccountId")
    private Long loanAccountId;

    @Column(name = "Subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "Description", length = 2000, nullable = false)
    private String description;

    @Column(name = "Status", length = 50, nullable = false)
    private String status;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "AttachmentPath", length = 1000)
    private String attachmentPath;

    public SupportTicket() {}
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getLoanAccountId() { return loanAccountId; }
    public void setLoanAccountId(Long loanAccountId) { this.loanAccountId = loanAccountId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
}
