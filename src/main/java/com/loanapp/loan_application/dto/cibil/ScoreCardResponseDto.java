package com.loanapp.loan_application.dto.cibil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCardResponseDto {

    private Long scoreCardId;
    private Long customerId;
    private String currentStatus;
    private String rejectionReason;
    private LocalDateTime appliedDate;
    private Integer cibilScore;
    private String riskCategory;
    private BigDecimal eligibleLoanAmount;


}
