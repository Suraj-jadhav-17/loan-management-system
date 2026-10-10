package com.loanapp.loan_application.dto.penalty;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PenaltyChargesDto {

    private Long penaltyChargeId;
    private Long emiScheduleId;
    private Long loanAccountId;
    private BigDecimal penaltyAmount;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}