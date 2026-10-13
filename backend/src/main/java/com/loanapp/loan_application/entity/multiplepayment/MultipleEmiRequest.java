package com.loanapp.loan_application.entity.multiplepayment;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class MultipleEmiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @Column(nullable = false)
    private Long loanAccountId;
    @Column(nullable = false)
    private String emiIds;
    private BigDecimal totalAmount;
    private String approvalStatus;
    private String paymentStatus;
    private LocalDateTime requestedDate;
    private LocalDateTime approvedDate;
}