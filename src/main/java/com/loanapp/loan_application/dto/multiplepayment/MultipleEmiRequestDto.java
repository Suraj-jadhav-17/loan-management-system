package com.loanapp.loan_application.dto.multiplepayment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MultipleEmiRequestDto {

    private Long requestId;
    private Long loanAccountId;
    private String emiIds;
    private BigDecimal totalAmount;
    private String approvalStatus;
    private String paymentStatus;
    private LocalDateTime requestedDate;
    private LocalDateTime approvedDate;
}