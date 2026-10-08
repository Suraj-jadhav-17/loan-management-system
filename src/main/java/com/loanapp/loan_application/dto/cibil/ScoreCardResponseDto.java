package com.loanapp.loan_application.dto.cibil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCardResponseDto {

    private Long scoreCardId;

    private Long customerId;

    private Integer cibilScore;

    private BigDecimal foir;

    private Integer incomeScore;

    private Integer employmentScore;

    private Integer ageScore;

    private Integer foirScore;

    private Integer totalScore;

    private BigDecimal eligibleLoanAmount;
}