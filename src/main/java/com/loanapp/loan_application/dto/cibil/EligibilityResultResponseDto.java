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
public class EligibilityResultResponseDto {

    private Long id;
    private Long customerId;
    private Integer cibilScore;
    private Boolean isEligible;
    private String decision;
    private String riskCategory;
    private String borrowingLimit;
    private BigDecimal loanAmount;
    private String rejectionReason;
}
