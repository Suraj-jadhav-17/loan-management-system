package com.loanapp.loan_application.dto.cibil;

import com.loanapp.loan_application.entity.cibil.RiskCategory;
import com.loanapp.loan_application.entity.cibil.ScoreCardStatus;
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
    private ScoreCardStatus currentStatus;
    private String rejectionReason;
    private LocalDateTime appliedDate;
    private Integer cibilScore;
    private RiskCategory riskCategory;
    private BigDecimal eligibleLoanAmount;
    private BigDecimal foir;
    private Integer incomeScore;
    private Integer employmentScore;
    private Integer ageScore;
    private Integer foirScore;
    private Integer totalScore;
}